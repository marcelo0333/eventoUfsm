from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import psycopg2
from psycopg2.extras import RealDictCursor
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np
import logging
import time

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="API de Recomendação - SmartEventos")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

DB_CONFIG = {
    "user": "postgres",
    "password": "1234",
    "host": "localhost",
    "port": "5432",
    "database": "eventsDB"
}

# Cache simples em memória
_cache = {}
CACHE_TTL = 600  # 10 minutos


def get_connection():
    return psycopg2.connect(**DB_CONFIG, cursor_factory=RealDictCursor)


# ------------------------------------------------------------------ #
#  HEALTH CHECK
# ------------------------------------------------------------------ #
@app.get("/health")
def health():
    return {"status": "ok"}


# ------------------------------------------------------------------ #
#  ENDPOINT PRINCIPAL
# ------------------------------------------------------------------ #
@app.get("/recommendations/{user_id}")
def get_recommendations(user_id: int, page: int = 0, size: int = 10):
    cache_key = f"rec_{user_id}_{page}_{size}"
    cached = _get_cache(cache_key)
    if cached:
        logger.info(f"[user {user_id}] retornando do cache")
        return cached

    try:
        content_scores = _content_based_tfidf(user_id)
        collab_scores  = _collaborative(user_id)
        cold_scores    = _cold_start(user_id)

        content_scores = _normalize(content_scores)
        collab_scores  = _normalize(collab_scores)
        cold_scores    = _normalize(cold_scores)

        has_history = len(content_scores) > 0 or len(collab_scores) > 0

        all_events = set(content_scores) | set(collab_scores) | set(cold_scores)
        hybrid = {}

        for event_id in all_events:
            c = content_scores.get(event_id, 0)
            k = collab_scores.get(event_id, 0)
            s = cold_scores.get(event_id, 0)
            hybrid[event_id] = (0.5 * c + 0.3 * k + 0.2 * s) if has_history else s

        # Todos os IDs ordenados por score
        all_sorted_ids = sorted(hybrid, key=hybrid.get, reverse=True)

        # Fallback: sem histórico, busca recentes
        if not all_sorted_ids:
            all_sorted_ids = _fetch_recent_event_ids(size * 10)  # ajuste conforme sua impl.

        # Paginação manual
        total_elements = len(all_sorted_ids)
        total_pages    = max(1, -(-total_elements // size))  # ceil division
        start          = page * size
        end            = start + size
        page_ids       = all_sorted_ids[start:end]

        events = _fetch_events(page_ids) if page_ids else []

        result = {
            "content": events,
            "pageable": {
                "pageNumber": page,
                "pageSize": size,
                "sort": {"empty": True, "sorted": False, "unsorted": True},
                "offset": start,
                "unpaged": False,
                "paged": True,
            },
            "last": page >= total_pages - 1,
            "totalPages": total_pages,
            "totalElements": total_elements,
            "size": size,
            "number": page,
            "sort": {"empty": True, "sorted": False, "unsorted": True},
            "first": page == 0,
            "numberOfElements": len(events),
            "empty": len(events) == 0,
        }

        _set_cache(cache_key, result)
        return result

    except Exception as e:
        logger.error(f"Erro na recomendação user {user_id}: {e}")
        raise HTTPException(status_code=500, detail=str(e))

# ------------------------------------------------------------------ #
#  ENDPOINT DE EXPLICAÇÃO — útil para a defesa do TCC
# ------------------------------------------------------------------ #
@app.get("/recommendations/{user_id}/explain")
def explain(user_id: int):
    content = _normalize(_content_based_tfidf(user_id))
    collab  = _normalize(_collaborative(user_id))
    cold    = _normalize(_cold_start(user_id))

    has_history = len(content) > 0 or len(collab) > 0

    return {
        "user_id": user_id,
        "strategy": "hybrid" if has_history else "cold_start",
        "content_events":  len(content),
        "collab_events":   len(collab),
        "cold_events":     len(cold),
        "weights": {
            "content":       0.5 if has_history else 0,
            "collaborative": 0.3 if has_history else 0,
            "cold_start":    0.2 if has_history else 1.0
        },
        "has_history": has_history
    }


# ------------------------------------------------------------------ #
#  FILTRAGEM POR CONTEÚDO COM TF-IDF
#
#  TF-IDF: Term Frequency × Inverse Document Frequency
#
#  TF  = quantas vezes a keyword aparece no evento
#  IDF = log(total de eventos / eventos com essa keyword)
#
#  Resultado: keywords raras e específicas têm peso maior.
#  Ex: "machine learning" em 2 de 100 eventos → peso alto
#      "workshop" em 80 de 100 eventos         → peso baixo
#
#  Depois calculamos cosine similarity:
#  mede o ângulo entre o vetor do perfil do usuário
#  e o vetor de cada evento — quanto menor o ângulo,
#  mais similares são os interesses.
# ------------------------------------------------------------------ #
def _content_based_tfidf(user_id: int) -> dict:
    try:
        with get_connection() as conn:
            with conn.cursor() as cur:

                # 1. Busca todas as keywords de todos os eventos
                cur.execute("""
                    SELECT ek.event_id, string_agg(k.name_keywords, ' ') as keywords
                    FROM events_to_keywords ek
                    JOIN keywords k ON k.keyword_id = ek.keyword_id
                    GROUP BY ek.event_id
                """)
                all_events_keywords = cur.fetchall()

                if not all_events_keywords:
                    return {}

                event_ids  = [r["event_id"] for r in all_events_keywords]
                event_docs = [r["keywords"] for r in all_events_keywords]

                # 2. Busca keywords dos eventos que o usuário já interagiu
                cur.execute("""
                    SELECT string_agg(k.name_keywords, ' ') as keywords
                    FROM user_interactions ui
                    JOIN events_to_keywords ek ON ek.event_id = ui.event_id
                    JOIN keywords k ON k.keyword_id = ek.keyword_id
                    WHERE ui.user_id = %s

                    UNION ALL

                    SELECT string_agg(k.name_keywords, ' ') as keywords
                    FROM user_bookmarks ub
                    JOIN events_to_keywords ek ON ek.event_id = ub.event_id
                    JOIN keywords k ON k.keyword_id = ek.keyword_id
                    WHERE ub.user_id = %s
                """, (user_id, user_id))

                user_keywords_rows = cur.fetchall()
                user_keywords = " ".join(
                    r["keywords"] for r in user_keywords_rows if r["keywords"]
                )

                if not user_keywords.strip():
                    return {}

                # 3. Busca eventos já vistos para excluir
                cur.execute("""
                    SELECT event_id FROM user_interactions WHERE user_id = %s
                    UNION
                    SELECT event_id FROM user_bookmarks WHERE user_id = %s
                """, (user_id, user_id))
                seen_ids = {r["event_id"] for r in cur.fetchall()}

        # 4. Aplica TF-IDF
        # O corpus é: todos os eventos + perfil do usuário (último elemento)
        corpus = event_docs + [user_keywords]

        vectorizer = TfidfVectorizer(
            min_df=1,          # ignora keywords que aparecem em menos de 1 doc
            max_df=0.95,       # ignora keywords que aparecem em mais de 95% dos docs
            ngram_range=(1, 2) # considera unigramas e bigramas: "machine" e "machine learning"
        )

        tfidf_matrix = vectorizer.fit_transform(corpus)

        # Vetor do usuário é o último elemento
        user_vector   = tfidf_matrix[-1]
        events_matrix = tfidf_matrix[:-1]

        # 5. Calcula cosine similarity entre perfil do usuário e cada evento
        similarities = cosine_similarity(user_vector, events_matrix).flatten()

        # 6. Monta dicionário event_id → score, excluindo já vistos
        scores = {}
        for idx, event_id in enumerate(event_ids):
            if event_id not in seen_ids:
                score = float(similarities[idx])
                if score > 0:
                    scores[event_id] = score

        return scores

    except Exception as e:
        logger.error(f"Erro content_based_tfidf: {e}")
        return {}


# ------------------------------------------------------------------ #
#  FILTRAGEM COLABORATIVA
#
#  Lógica: usuários que interagiram/avaliaram os mesmos eventos
#  de forma parecida têm gostos similares.
#  Recomenda eventos que esses usuários similares gostaram
#  mas o usuário atual ainda não viu.
# ------------------------------------------------------------------ #
def _collaborative(user_id: int) -> dict:
    try:
        with get_connection() as conn:
            with conn.cursor() as cur:

                cur.execute("""
                    SELECT similar_user, COUNT(*) as score
                    FROM (
                        SELECT r2.user_id as similar_user
                        FROM user_rating r1
                        JOIN user_rating r2
                            ON r1.event_id = r2.event_id
                            AND r2.user_id != %s
                            AND ABS(r1.rating - r2.rating) <= 1
                        WHERE r1.user_id = %s

                        UNION ALL

                        SELECT b2.user_id as similar_user
                        FROM user_bookmarks b1
                        JOIN user_bookmarks b2
                            ON b1.event_id = b2.event_id
                            AND b2.user_id != %s
                        WHERE b1.user_id = %s
                    ) similares
                    GROUP BY similar_user
                    ORDER BY score DESC
                    LIMIT 10
                """, (user_id, user_id, user_id, user_id))

                similar_users = [r["similar_user"] for r in cur.fetchall()]

                if not similar_users:
                    return {}

                cur.execute("""
                    SELECT event_id, AVG(score) as avg_score
                    FROM (
                        SELECT event_id, rating as score
                        FROM user_rating
                        WHERE user_id = ANY(%s) AND rating >= 3

                        UNION ALL

                        SELECT event_id, 3 as score
                        FROM user_bookmarks
                        WHERE user_id = ANY(%s)
                    ) scores
                    WHERE event_id NOT IN (
                        SELECT event_id FROM user_interactions WHERE user_id = %s
                        UNION
                        SELECT event_id FROM user_bookmarks WHERE user_id = %s
                    )
                    GROUP BY event_id
                    ORDER BY avg_score DESC
                """, (similar_users, similar_users, user_id, user_id))

                return {r["event_id"]: float(r["avg_score"]) for r in cur.fetchall()}

    except Exception as e:
        logger.error(f"Erro collaborative: {e}")
        return {}


# ------------------------------------------------------------------ #
#  COLD START
#
#  Para usuários novos: usa preferências do cadastro
#  e popularidade geral como fallback.
# ------------------------------------------------------------------ #
def _cold_start(user_id: int) -> dict:
    try:
        with get_connection() as conn:
            with conn.cursor() as cur:

                cur.execute("""
                    SELECT preferred_types, course, preferred_center
                    FROM user_preferences
                    WHERE user_id = %s
                """, (user_id,))

                prefs = cur.fetchone()
                scores = {}

                if prefs and prefs["preferred_types"]:
                    type_ids = [
                        int(t) for t in prefs["preferred_types"].split(",")
                        if t.strip()
                    ]

                    if type_ids:
                        cur.execute("""
                            SELECT e.event_id,
                                   COALESCE(e.total_bookmarks, 0)  as bookmarks,
                                   COALESCE(e.average_rating, 0)   as rating
                            FROM events e
                            WHERE e.type_id = ANY(%s)
                            AND e.event_id NOT IN (
                                SELECT event_id FROM user_interactions WHERE user_id = %s
                                UNION
                                SELECT event_id FROM user_bookmarks  WHERE user_id = %s
                            )
                            ORDER BY bookmarks DESC, rating DESC
                        """, (type_ids, user_id, user_id))

                        for r in cur.fetchall():
                            scores[r["event_id"]] = (
                                float(r["bookmarks"]) * 0.6 +
                                float(r["rating"])    * 0.4
                            )

                if not scores:
                    cur.execute("""
                        SELECT event_id,
                               COALESCE(total_bookmarks, 0) as bookmarks,
                               COALESCE(average_rating, 0)  as rating
                        FROM events
                        WHERE event_id NOT IN (
                            SELECT event_id FROM user_interactions WHERE user_id = %s
                            UNION
                            SELECT event_id FROM user_bookmarks  WHERE user_id = %s
                        )
                        ORDER BY bookmarks DESC, rating DESC
                        LIMIT 20
                    """, (user_id, user_id))

                    for r in cur.fetchall():
                        scores[r["event_id"]] = (
                            float(r["bookmarks"]) * 0.6 +
                            float(r["rating"])    * 0.4
                        )

                return scores

    except Exception as e:
        logger.error(f"Erro cold_start: {e}")
        return {}


# ------------------------------------------------------------------ #
#  HELPERS
# ------------------------------------------------------------------ #
def _normalize(scores: dict) -> dict:
    """Normaliza scores entre 0 e 1 para permitir combinação justa."""
    if not scores:
        return {}
    max_val = max(scores.values())
    if max_val == 0:
        return scores
    return {k: v / max_val for k, v in scores.items()}


def _get_cache(key: str):
    if key in _cache:
        result, timestamp = _cache[key]
        if time.time() - timestamp < CACHE_TTL:
            return result
    return None


def _set_cache(key: str, value):
    _cache[key] = (value, time.time())


def _fetch_events(event_ids: list) -> list:
    if not event_ids:
        return []
    try:
        with get_connection() as conn:
            with conn.cursor() as cur:
                cur.execute("""
                    SELECT
                        e.event_id        AS "eventsId",
                        e.event_name      AS "eventName",
                        e.description,
                        e.center_name     AS "centerName",
                        e.date_initial    AS "dateInitial",
                        e.date_final      AS "dateFinal",
                        e.image_event     AS "imgEvent",
                        e.average_rating  AS "averageRating",
                        e.total_bookmarks AS "totalBookmarks",
                        t.name_types      AS "typeName"
                    FROM events e
                    LEFT JOIN type_events t ON t.types_id = e.type_id
                    WHERE e.event_id = ANY(%s)
                """, (event_ids,))
                results = cur.fetchall()
                order = {eid: i for i, eid in enumerate(event_ids)}
                return sorted(results, key=lambda r: order.get(r["eventsId"], 999))
    except Exception as e:
        logger.error(f"Erro fetch_events: {e}")
        return []


def _fetch_recent_events(limit: int) -> list:
    try:
        with get_connection() as conn:
            with conn.cursor() as cur:
                cur.execute("""
                    SELECT
                        e.event_id        AS "eventsId",
                        e.event_name      AS "eventName",
                        e.description,
                        e.center_name     AS "centerName",
                        e.date_initial    AS "dateInitial",
                        e.date_final      AS "dateFinal",
                        e.image_event     AS "imgEvent",
                        e.average_rating  AS "averageRating",
                        e.total_bookmarks AS "totalBookmarks"
                    FROM events e
                    ORDER BY e.date_initial DESC
                    LIMIT %s
                """, (limit,))
                return cur.fetchall()
    except Exception as e:
        logger.error(f"Erro fetch_recent: {e}")
        return []