# Deploy do eventosUfsm no Cloud Run (Always Free)

Passo a passo partindo de uma conta GCP zerada. Só cobre o **eventosUfsm**
(API Java). As outras 4 peças do projeto (`recommendationApi`, `rake_keywords`,
`scrapper_events_ufsm`, `eventosFront`) ficam para uma rodada seguinte.

## 0. Antes de tudo: onde fica o banco?

Este deploy assume que você já tem um Postgres acessível pela internet
(`DB_URL`/`DB_USER`/`DB_PASSWORD`). **Cloud SQL não está no Always Free** —
cobra 24/7 mesmo sem uso. Se ainda não decidiu, as opções mais comuns pra
ficar em custo zero permanente são [Neon](https://neon.tech) (Postgres
serverless, escala a zero) ou [Supabase](https://supabase.com) (free tier
fixo, pausa após 1 semana sem uso). Qualquer um dos dois funciona sem
mudar nada no código — só troca o valor de `DB_URL`.

## 1. Habilitar as APIs necessárias

```bash
gcloud config set project SEU_PROJECT_ID

gcloud services enable \
  run.googleapis.com \
  cloudbuild.googleapis.com \
  artifactregistry.googleapis.com \
  secretmanager.googleapis.com
```

- `run.googleapis.com` — Cloud Run em si.
- `cloudbuild.googleapis.com` — builda a imagem a partir do Dockerfile (tem
  free tier próprio: 120 minutos de build/dia).
- `artifactregistry.googleapis.com` — onde a imagem gerada fica armazenada.
- `secretmanager.googleapis.com` — guarda `DB_PASSWORD` e `JWT_SECRET`.

## 2. Autenticar

```bash
gcloud auth login
gcloud auth application-default login   # necessário pro `gcloud builds submit`
```

Se o projeto não tem billing habilitado ainda, habilite em
console.cloud.google.com/billing — o Always Free **exige** uma conta de
billing associada, mesmo que o uso final seja R$ 0,00.

## 3. Criar os segredos no Secret Manager

Só os dois valores realmente sensíveis vão para o Secret Manager. O resto
(`DB_URL`, `DB_USER`, `CORS_ALLOWED_ORIGINS`) vai como variável de ambiente
comum no deploy — não são credenciais, só endereço/username em texto plano.

```bash
# Senha do banco (Neon/Supabase te dão esse valor no dashboard)
printf '%s' 'sua-senha-aqui' | gcloud secrets create eventos-db-password --data-file=-

# Chave de assinatura do JWT — gere uma nova, NÃO reaproveite a de dev.
# Precisa ser Base64 de pelo menos 256 bits (32 bytes) pro HS256.
openssl rand -base64 32 | tr -d '\n' | gcloud secrets create eventos-jwt-secret --data-file=-
```

Para atualizar um segredo depois (rotação):

```bash
printf '%s' 'nova-senha' | gcloud secrets versions add eventos-db-password --data-file=-
```

## 4. Primeiro deploy

```bash
cd eventosUfsm
PROJECT_ID=seu-project-id \
DB_URL='jdbc:postgresql://SEU_HOST:5432/eventsDB?sslmode=require' \
DB_USER='seu_usuario' \
CORS_ALLOWED_ORIGINS='https://seu-frontend.com' \
./deploy.sh
```

O script builda a imagem via Cloud Build, faz o deploy no Cloud Run em
`us-central1` (única região garantida no Always Free) e imprime a URL do
serviço no final. Os detalhes de cada flag `gcloud run deploy` estão
comentados dentro do `deploy.sh`.

Deploys seguintes: rode o mesmo comando de novo (ele recria a revisão).

## 5. Verificar que subiu

```bash
curl https://SUA-URL.a.run.app/api/actuator/health/liveness
# {"status":"UP"}
```

Isso NÃO consulta o banco — só confirma que a JVM está de pé. Pra checar o
banco também: `.../api/actuator/health/readiness`.

## 6. Acompanhar o consumo do free tier

- Console: **Cloud Run → [seu serviço] → Métricas** mostra requisições,
  tempo de CPU e memória usados no período de billing atual.
- **Faturamento → Relatórios**, filtrando por SKU "Cloud Run" — mostra se
  algum mês passou dos 2 milhões de requisições ou das 360.000 GiB-s.
- Recomendado: crie um **orçamento com alerta** (Faturamento → Orçamentos e
  alertas) de R$ 1,00, só pra ser avisado por e-mail caso algo escape do
  free tier — com `--min-instances=0` e `--max-instances=3` o risco é baixo,
  mas não é zero se o serviço receber tráfego anômalo.

## Limitações conhecidas (não corrigidas neste deploy)

- **Upload de imagens é efêmero.** `AuthService`/`EventsService` gravam
  arquivos em `app.image-directory` (`/tmp/images` por padrão). No Cloud
  Run, `/tmp` é local à instância do container — some quando a instância é
  reciclada (o que acontece regularmente com `min-instances=0`) e não é
  compartilhado entre instâncias concorrentes. Upload de foto de perfil ou
  imagem de evento vai "sumir" depois de um tempo. Corrigir isso exige
  migrar esses dois pontos para um bucket (Cloud Storage tem free tier —
  5GB — mas isso é uma dependência nova e uma mudança de código; não
  implementei por não estar aprovado ainda).
- **Cold start.** Com `min-instances=0`, a primeira requisição após um
  período ocioso paga o boot da JVM + Spring Boot (tipicamente 3-6s nesse
  projeto). Para uma API acadêmica de baixo tráfego isso costuma ser
  aceitável. Se incomodar, as opções são, em ordem de esforço: (a)
  `--min-instances=1` (sai do scale-to-zero, deixa de ser 100% grátis mas
  elimina cold start), (b) habilitar Class Data Sharing (CDS) da própria
  JVM sem trocar de runtime, (c) reescrever para GraalVM native image
  (cold start sub-segundo, mas é uma mudança grande de build/runtime —
  avise se quiser que eu detalhe essa opção antes de implementar).

## Variáveis de ambiente — lista final

| Variável | Onde definir | Obrigatória | Observação |
|---|---|---|---|
| `DB_URL` | env var comum (`--set-env-vars` no deploy.sh) | Sim | URL JDBC do Postgres externo |
| `DB_USER` | env var comum | Sim | Usuário do Postgres |
| `DB_PASSWORD` | **Secret Manager** (`eventos-db-password`) | Sim | Sem fallback — app não sobe sem ela |
| `JWT_SECRET` | **Secret Manager** (`eventos-jwt-secret`) | Sim | Base64, 256+ bits; sem fallback |
| `CORS_ALLOWED_ORIGINS` | env var comum | Sim (prod) | Default só cobre localhost/dev; em prod precisa do domínio real do frontend |
| `PORT` | injetada automaticamente pelo Cloud Run | Automática | Não definir manualmente |
| `SERVER_PORT` | — | Não | Deixe **não definida** em produção; existe só pro fluxo local/docker-compose (tem prioridade sobre `PORT` se setada, então setá-la em prod quebraria o Cloud Run) |
| `IMAGE_DIR` | env var comum | Não | Default `/tmp/images` já funciona; ver limitação de efemeridade acima |
| `SCRAPPER_SCRIPT_PATH` / `SCRAPPER_WORK_DIR` | env var comum | Não | Deixe vazias em prod — o scraper Node não roda dentro deste container |
