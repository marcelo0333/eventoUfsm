# Plano de Refatoração — eventoUfsm

---

## STATUS DE EXECUÇÃO — Sessão 7 (rodar no celular + docker-compose)

Legenda: ✅ feito e verificado. **Verificação**: APK `assembleDebug` → BUILD SUCCESSFUL; backend `mvnw compile` OK; compose validado (YAML).

**Rodar no celular (Android):**
- ✅ APK de debug compila de ponta a ponta (`app-debug.apk` gerado). No caminho, corrigidos: `MainActivity.java` (registrava plugin de notificação antigo inexistente → simplificado pro padrão Capacitor 6); `AndroidManifest.xml` (removido receiver fantasma, liberado `usesCleartextTraffic`, adicionada permissão `POST_NOTIFICATIONS`); `@capacitor/google-maps` removido (não usado, exigiria API key e travaria o Gradle); `MainActivity` package/namespace conferidos; `local.properties` criado apontando pro SDK.
- ✅ `environment.ts` aponta para o IP da LAN (`192.168.0.102`) em vez de `localhost`.
- ✅ Guia **`RODAR_NO_CELULAR.md`** (na raiz) com passo-a-passo, rebuild e troubleshooting.
- ⚠️ Nota de ambiente: Gradle 8.2.1 **não** funciona com JDK 22 (o do PATH) — usar JDK 17 (`D:/programas/Java/jdk-17`); no Android Studio é automático (JBR embutido).

**docker-compose consolidado (Fase 5.2):**
- ✅ `docker-compose.yml` da raiz refeito: backend na **porta 9090:9090** (antes 8080, inalcançável), `JWT_SECRET` passado via env, **healthcheck do Postgres** + `depends_on: condition: service_healthy`, interpolação via `.env` com defaults de dev. `rake`/`scraper` movidos para o profile `tools` (rodam sob demanda).
- ✅ **Dockerfile do backend corrigido**: runtime alinhado ao builder (`eclipse-temurin:17-jre`, era `openjdk:22`), `EXPOSE 9090`.
- ✅ **Dockerfiles criados** para `recommendationApi` (FastAPI/uvicorn) e `rake_keywords` (job único) — antes não existiam.
- ✅ `eventosUfsm/docker-compose.yml` (duplicado, com porta invertida `5432:5433` e DB via `localhost`) **removido**.
- ✅ Backend robusto pra subir sem config: `scrapper.script.path`/`working.dir` ganharam default vazio (antes falha de inicialização se ausentes) e o `@Scheduled` do scrapper pula limpo quando não configurado; `jwt.secret` lê `${JWT_SECRET:<dev-default>}`.

---

## STATUS DE EXECUÇÃO — Sessão 6 (geocoding do mapa)

Legenda: ✅ feito e verificado. **Verificação**: `ng build` OK + query validada contra a API real do Nominatim.

- ✅ **Geocoding do mapa corrigido** (`maps.service.ts`): os dados vêm sujos (`city="Recife - Pernambuco"`, `street="Av. Alfredo Lisboa - 0 - Armazen 10"`) e a busca **estruturada** do Nominatim retornava `[]` → o mapa caía sempre nas coordenadas padrão. Adicionados `parseCityState()` (separa cidade/estado) e `cleanStreet()` (extrai o nome da via, descarta lixo), e a cadeia agora **prioriza a busca em texto livre `q`** (muito mais tolerante). Testado: estruturada = `[]`; free-text limpo = coordenadas corretas.
  - ⬜ **Melhoria recomendada**: fazer o geocoding no **backend/scraper** e persistir `latitude`/`longitude` no banco (o serviço já usa essas coords primeiro quando existem). Evita depender do Nominatim no cliente e sua política de uso (~1 req/s, sem `User-Agent` no navegador).

---

## STATUS DE EXECUÇÃO — Sessão 5 (perfil + detalhe do evento)

Legenda: ✅ feito e verificado. **Verificação**: `ng build` → bundle generation complete.

- ✅ **Página de usuário/perfil redesenhada**: cabeçalho com gradiente, avatar com iniciais (fallback quando não há foto), badge de admin, e lista de ações (`Editar perfil`, `Criar evento` só admin, `Sair`). Modal de edição alinhado ao design system. Adicionado `imgUser` ao `UserModel`.
- ✅ **`UserDetailService.editUser` simplificado**: removido o header `Authorization` manual (o `authInterceptor` já anexa o token corretamente agora).
- ✅ **Detalhe do evento polido**: capa com badge de favoritos sobreposto, título/centro, linhas de info (local/data com ícones), seções de descrição e localização (mapa com cantos arredondados), seção de contato — substituindo os `<hr>`, `color: aqua` e estilos inline. Ações do toolbar reorganizadas (voltar à esquerda; favoritar/lembrete/editar/excluir à direita). Mapa Leaflet (`#map`) preservado.

---

## STATUS DE EXECUÇÃO — Sessão 4 (estados de UI + UX)

Legenda: ✅ feito e verificado. **Verificação**: `ng build` → bundle generation complete.

- ✅ **Componente `slides` refatorado**: eliminada a triplicação (3 blocos quase idênticos → 1 template com getter `items`), removidos os `console.log`, e adicionados **skeleton de carregamento** e **estado vazio** (mensagem por tipo). Cards com imagem `object-fit: cover` e estilo do design system.
- ✅ **Home page**: flags de loading por seção (`loadingEvents/Recommended/Bookmarks`) passadas ao `slides`; carregamento com `forkJoin` + `catchError` por chamada (uma seção que falhe — ex. API de recomendação fora — não derruba as outras).
- ✅ **Pull-to-refresh corrigido**: `doRefresh` completava com `setTimeout(3000)` fixo; agora completa quando as respostas reais chegam (via `complete` do `forkJoin`).
- ✅ **Reminders**: estados de loading/vazio + card redesenhado (imagem clicável, horário com ícone, botão de excluir alinhado).
- ✅ **Filter-item** (favoritos/categorias): estados de loading/vazio com mensagem contextual.

---

## STATUS DE EXECUÇÃO — Sessão 3 (integração nativa mobile)

Legenda: ✅ feito e verificado · 🟡 parcial · ⬜ pendente. **Verificação**: `ng build` → bundle generation complete após todas as mudanças (não testado em dispositivo físico).

- ✅ **Plugins Capacitor instalados**: `@capacitor/preferences`, `@capacitor/network`, `@capacitor/local-notifications`, `@capacitor/splash-screen` (v6).
- ✅ **Rebrand**: `capacitor.config.ts` com `appId: br.ufsm.smarteventos`, `appName: SmartEventos`, config de SplashScreen e LocalNotifications; `android/app/build.gradle` `applicationId` e `strings.xml` (`app_name`/`title`) atualizados.
  - ⬜ **Pendente**: o `namespace`/package Java do Android continua `io.ionic.starter` — renomear exige mover `MainActivity.java` de pasta, não fiz sem poder buildar o Android. Ícone/splash ainda são placeholders (gerar com `@capacitor/assets`).
- ✅ **Inicialização nativa** (`app.component.ts`, guardada por `Capacitor.isNativePlatform()`): StatusBar (cor/estilo do tema), botão físico de voltar do Android (volta na pilha ou minimiza), `SplashScreen.hide()` após carregar.
- ✅ **Notificações migradas** de Cordova descontinuado para `@capacitor/local-notifications` (funciona Android **e** iOS): permissões, agendamento e cancelamento. Pacotes Cordova (`@awesome-cordova-plugins/*`, `cordova-plugin-local-notification`) **desinstalados** (conflitariam com o plugin Capacitor no `cap sync`); providers removidos do `app.module.ts`.
- ✅ **Rede/offline**: `NetworkService` (`@capacitor/network` no nativo, `navigator.onLine` no web) + banner de "sem conexão" no shell do app.
- 🟡 **Secure storage do token — DEFERIDO conscientemente**: `@capacitor/preferences` é assíncrono, mas interceptor/guards/`tokenValid` leem o token de forma síncrona; a migração exigiria refactor async de todo esse caminho, com risco real sem teste em dispositivo. O bug funcional de storage (chave errada) já foi corrigido na sessão 1; `localStorage` funciona na WebView. Fica para a passada de hardening.

**Ainda pendente após esta sessão**: ícones/splash reais, migração async para secure storage, rename do package Android, estados de loading/erro/vazio consistentes em todas as páginas, testes (front e back), e Fases 3/4/5 (Python/scraper/infra).

---

## STATUS DE EXECUÇÃO — Sessão 2 (back + front + design)

Legenda: ✅ feito e verificado · 🟡 parcial · ⬜ pendente. **Verificação**: backend `./mvnw compile` → BUILD SUCCESS e frontend `ng build` → bundle generation complete, ambos após todas as mudanças.

**Backend (Fase 0.4 + Fase 1):**
- ✅ **IDOR corrigido** em todas as rotas de dados de usuário. `userId` agora é derivado do JWT via `@AuthenticationPrincipal User` (nunca do path/body): `BookmarksRoute`, `ReminderRoute` (+ `isOwnedBy`), `CommentsRoute`/`CommentsService`, `RatingRoute`/`RatingService`, `UserInteractionRoute`, `UserPreferencesRoute`. Escritas usam o id do token; leituras rejeitam (403) id de outro usuário.
- ✅ **CORS centralizado** no `SecurityConfig` (`corsConfigurationSource` + allowlist via `${cors.allowed-origins}`); removido `@CrossOrigin("*")` de todos os controllers.
- ✅ **`GlobalExceptionHandler`** (`@RestControllerAdvice`) com corpo de erro padronizado `{timestamp,status,error,message}`.
- ✅ **`EAGER`→`LAZY`** nas 4 coleções `@JsonIgnore` de `User` (bookmarks/comments/ratings/reminders); `createdEvents` mantido EAGER de propósito (é serializado no login — LAZY quebraria).
- ✅ Removido `System.out.println` sensível (resposta de login inteira ia pro stdout) e limpeza de imports/campos mortos em `AuthRoute`.

**Frontend (Fase 2 + integração + design):**
- ✅ **Guards reais**: `authGuard` (sessão válida, aplicado a `/tabs`) e `adminGuard` (papel ADMIN, aplicado a `events-creator`). Antes `IsAdminGuard` sempre retornava `true`.
- ✅ **Integração registro→preferências**: como o endpoint de preferências passou a exigir auth, o fluxo de cadastro agora guarda o token retornado para autenticar a chamada de preferências e navega só após concluir (antes era fire-and-forget + navegação imediata).
- ✅ **Design system novo**: tema completo em `theme/variables.scss` (paleta azul institucional + esmeralda + violeta, tokens de raio/sombra/gap, modo escuro); `global.scss` com safe-areas, `.section-title`, cards/botões padronizados e estilos de auth compartilhados; `home-page`, `login` e `register` reescritos usando o novo sistema (hero com gradiente, campos consistentes, categorias com botão em gradiente).

**Notas para a próxima passada:**
- ⬜ O componente `event-card` é boilerplate morto (imagem de montanhas / "Card Title") mas **não é referenciado em lugar nenhum** — não é renderizado. Pode ser implementado de verdade ou removido.
- ⬜ Restam: plugins nativos Capacitor, notificações, secure storage, migrations Flyway, testes, Fases 3/4/5 (Python/scraper/infra) — ver seções abaixo.

---

## STATUS DE EXECUÇÃO (sessão anterior — segurança + bugs críticos)

Legenda: ✅ feito e verificado · 🟡 parcial · ⬜ pendente

**Verificações executadas nesta sessão**: backend compila (`./mvnw compile` → BUILD SUCCESS) e frontend passa no typecheck (`npx tsc --noEmit -p tsconfig.app.json` → exit 0), ambos após todas as mudanças abaixo. **Não** foi feito: teste de integração rodando, build de APK/IPA, nem teste em dispositivo físico (não há dispositivo/simulador neste ambiente).

### Fase 0 — Segurança
- ✅ **0.1** `.env` e `eventosUfsm/.env` removidos do índice do git (`git rm --cached`); `.gitignore` (raiz e backend) agora ignoram `.env`; criados `.env.example` em raiz, `eventosUfsm/`, `recommendationApi/`, `rake_keywords/`, `scrapper_events_ufsm/`.
  - ⬜ **PENDENTE MANUAL (destrutivo)**: purgar o `.env` do histórico do git (`git filter-repo`/BFG) e **force-push** — não executado por ser destrutivo em repo com colaborador (`marcelo0333`). Rotacione a senha `1234` de qualquer forma. Os commits `f5a3051` e `d4ca301` ainda contêm o `.env` no histórico.
- ✅ **0.2** JWT secret movido de literal em `JwtService.java` para `@Value("${jwt.secret}")`; `application.properties` lê `jwt.secret=${JWT_SECRET}` (sem default → falha se ausente); `JWT_SECRET` documentado no `.env.example`.
- ✅ **0.3** Endpoint `POST /auth/privilege` e método `AuthService.privilege()` removidos; checkbox "Registrar como admin" e método `RegisterService.privilege()` removidos do frontend.
- 🟡 **0.4** `SecurityConfig` travado: acesso anônimo a mutações/dados de usuário agora exige autenticação (`.anyRequest().authenticated()`; só navegação GET de eventos/locais/imagens e login/register/refresh continuam públicos).
  - ⬜ **PENDENTE (IDOR)**: derivar o `userId` do JWT dentro dos services (`UserInteractionService`, ratings, reminders, bookmarks, comments, preferences) em vez de confiar no `userId` vindo de path/body. Hoje um usuário **autenticado** ainda pode passar o ID de outro. Requer mudança coordenada front+back (endpoints tipo `/preferences/{userId}`, `/bookmarks/{userId}`). Não feito — é grande e precisa de teste de integração.
- ✅ **0.5** `@JsonIgnore` no campo `password` e no getter `getPassword()` de `User.java` (o getter explícito exigia anotar os dois).
- ✅ **0.6** Credenciais hardcoded removidas de `recommendation_api.py`, `keywordExtraction.py` e `data-source.ts` (agora via env vars); CORS `*` do `recommendationApi` trocado por allowlist via `CORS_ALLOWED_ORIGINS`; import morto `rake_nltk` removido.
  - ⬜ Falta carregar o `.env` em runtime nos serviços Python/scraper (ex. `python-dotenv`, `dotenv/config`) para execução local fora do Docker — ver Fases 3/4.

### Fase 2 — Frontend (itens críticos já feitos; restante pendente)
- ✅ **2.2 (parcial)** Aba "Usuário" agora aponta para `/tabs/user` (havia uma `user.page` real, mas o botão apontava para o boilerplate `/tabs/tab3`); aba "Lembretes" corrigida de `/tabs/reminder/{userId}` (rota inexistente) para `/tabs/reminders`; diretórios órfãos `tab2/` e `tab3/` removidos. (`explore-container` foi mantido — é usado por slides/filter-item/home-page.)
- ✅ **2.3** `environment.prod.ts` não aponta mais para `localhost` (placeholder HTTPS com aviso) e ganhou a chave `recommendationApi` que faltava. **Requer preencher a URL real de produção antes do build de release.**
- ✅ **2.4 (bug funcional)** Interceptor `auth-interceptor.service.ts` corrigido: lia `localStorage.getItem('accessToken')` (chave inexistente) — agora lê o `accessToken` do blob JSON sob `environment.tokenKey`, então o header `Authorization` passa a ser anexado de fato. (Leitura direta do storage para evitar dependência circular com `HttpClient`.)
  - ⬜ Falta a parte de **secure storage** (migrar de `localStorage` para `@capacitor/preferences`) — refactor assíncrono, deixado para a passada de hardening mobile.
- ✅ **2.5** `tokenValid()` corrigido de `exp * 2000` para `exp * 1000`.

### Ainda totalmente pendentes
- ⬜ Fase 1 inteira (CORS backend, migrations Flyway, `EAGER`→`LAZY`, testes, `@RestControllerAdvice`, logging SLF4J, perfis de ambiente).
- ⬜ Fase 2 restante (rebrand Capacitor/ícones/splash, plugins nativos reais, notificações Capacitor, estados de rede/offline, navegação nativa, guards reais, testes, build+teste em dispositivo).
- ⬜ Fase 3 (deploy/Docker/tests dos serviços Python; guard `__main__` do `keywordExtraction.py`).
- ⬜ Fase 4 (entrypoint do scraper — **ele não roda hoje**; robustez/retry; migrations TypeORM).
- ⬜ Fase 5 (`.git` aninhados; unificar docker-compose; CI/CD; Dockerfiles).

> **Nota**: as mudanças estão staged/no working tree mas **não foram commitadas** — revise e faça o commit você mesmo. Um `git status` mostra o que mudou.

---

## Como usar este documento

Este documento é uma especificação técnica completa para levar o **eventoUfsm** (plataforma de recomendação de eventos da UFSM) de protótipo acadêmico a um sistema pronto para produção em **Android e iOS reais** (hoje o front é essencialmente um site Ionic nunca testado fora do navegador), com backend seguro e infraestrutura consolidada.

Foi escrito para ser executado por um agente de codificação autônomo, sem depender de contexto de conversa anterior. Cada item tem: problema, localização, o que fazer, e critério de aceite. Trabalhe fase por fase, na ordem — fases posteriores assumem que as anteriores foram concluídas (ex.: não faz sentido testar em dispositivo real com a API ainda apontando para `localhost`).

**Regra geral**: não introduza abstrações, dependências ou features além do que está pedido aqui. Corrija o que está listado; não "aproveite para melhorar" código não relacionado.

**Estrutura do monorepo**:
```
eventoUfsm/
├── eventosFront/        Ionic/Angular + Capacitor (Android/iOS)
├── eventosUfsm/         Backend Spring Boot (Java 17)
├── recommendationApi/   Microsserviço Python (FastAPI) — recomendação
├── rake_keywords/       Script Python — extração de keywords
└── scrapper_events_ufsm/ Scraper TypeScript/Node — eventos do site da UFSM
```

---

## FASE 0 — Segurança crítica (bloqueante, fazer antes de qualquer outra coisa)

### 0.1 Remover segredos do histórico do git
**Problema**: `eventosUfsm/.env` e o `.env` da raiz estão commitados no git (confirmado via `git ls-files`), contendo senha de banco de dados e paths locais absolutos. `.gitignore` não lista `.env` em nenhum lugar do repo.

**Ação**:
1. Adicionar ao `.gitignore` (raiz e `eventosUfsm/`): `.env`, `*.env`, exceto `.env.example`.
2. `git rm --cached .env eventosUfsm/.env`.
3. Criar `.env.example` em cada serviço com as chaves necessárias e valores placeholder (`DB_PASSWORD=changeme`).
4. Rotacionar toda credencial real que estiver nesses arquivos (mesmo sendo `1234` — tratar como comprometida).
5. Limpar o histórico do git com `git filter-repo --path .env --path eventosUfsm/.env --invert-paths` (ou BFG Repo-Cleaner). **Isso reescreve o histórico — avisar antes de fazer push forçado.**

**Critério de aceite**: `git log --all --full-history -- .env eventosUfsm/.env` não retorna nenhum commit. `git status` limpo com `.env` presente localmente mas ignorado.

### 0.2 Remover JWT secret hardcoded
**Localização**: `eventosUfsm/src/main/java/com/events/eventosUfsm/middleware/auth/JwtService.java:27`

**Problema**: `SECRET_KEY` é uma string Base64 literal no código-fonte. Qualquer pessoa com acesso ao repositório pode forjar tokens JWT válidos, inclusive de admin.

**Ação**: Mover para variável de ambiente via `@Value("${jwt.secret}")` lido de `application.properties` (`jwt.secret=${JWT_SECRET}`), sem valor default em produção. Gerar uma chave nova de 256+ bits e documentar no `.env.example`.

**Critério de aceite**: `grep -r "SECRET_KEY" --include=*.java` não retorna nenhuma string literal de chave.

### 0.3 Eliminar escalonamento de privilégio sem autenticação
**Localização**: `AuthService.java:132` (`privilege` method), `SecurityConfig.java:27` (`.requestMatchers(HttpMethod.POST, "/auth/**").permitAll()`).

**Problema**: `POST /auth/privilege` cria um usuário com `Role.ADMIN` direto do corpo da requisição, e está liberado para qualquer um (`permitAll` cobre todo `/auth/**`). Combinado com o checkbox "Registrar como admin" exposto no formulário de cadastro do frontend (`register.page.html`), qualquer usuário anônimo vira admin.

**Ação**:
1. Remover o endpoint `/auth/privilege` completamente, OU restringi-lo a `hasRole("ADMIN")` explicitamente no `SecurityConfig` (não dentro de `/auth/**` genérico).
2. Remover o checkbox "Registrar como admin" do formulário público de cadastro (`eventosFront/src/app/pages/register/register.page.html`).
3. Criar promoção de admin apenas via endpoint protegido chamado por um admin já autenticado, ou via seed/migration manual.

**Critério de aceite**: Um usuário não autenticado não consegue, por nenhuma rota, criar ou se tornar ADMIN.

### 0.4 Travar `SecurityConfig` — parar de confiar em `permitAll`
**Localização**: `eventosUfsm/src/main/java/com/events/eventosUfsm/middleware/security/SecurityConfig.java`

**Problema**: quase todo endpoint que muda dado (avaliações, comentários, lembretes, bookmarks, interações) está `permitAll`. Os IDs de usuário vêm do path/body da requisição, não do JWT — isso é IDOR: qualquer um pode editar/deletar dados de outro usuário sabendo o ID.

**Ação**:
1. Trocar todos os `permitAll` de endpoints de mutação (`POST`/`PUT`/`DELETE` fora de login/cadastro/consulta pública de eventos) para `.authenticated()`.
2. Em cada Service/Route que hoje recebe `userId` como parâmetro de path/body para operações de escrita (`UserInteractionService`, `UserRatingRepository`-related routes, `ReminderRoute`, `BookmarkRoute`, `CommentRoute`), passar a extrair o `userId` do JWT autenticado (via `Authentication`/`SecurityContext`) em vez de confiar no valor enviado pelo cliente. Ignorar/rejeitar qualquer `userId` inconsistente vindo do corpo.
3. Corrigir `AuthFilter.java` para rejeitar (401) requisições com `Authorization` ausente/malformado em rotas autenticadas, em vez de deixar passar silenciosamente.

**Critério de aceite**: Testar manualmente (ou via teste de integração) que o usuário A não consegue deletar/editar um comentário, avaliação, lembrete ou bookmark do usuário B mesmo enviando o ID de B na requisição.

### 0.5 Parar de vazar senha na resposta de login
**Localização**: `AuthRoute.SignInResponse`, `model/user/User.java`.

**Problema**: `User` é uma entidade JPA serializada diretamente na resposta de login, sem `@JsonIgnore` no campo `password` (só há proteção em relações, não no campo em si).

**Ação**: Anotar `password` com `@JsonIgnore` em `User.java`, OU (preferível) criar um DTO `UserResponseDTO` sem o campo de senha e usá-lo em toda resposta que serializa `User`.

**Critério de aceite**: A resposta JSON de `/auth/login` e de qualquer endpoint que retorne `User` nunca contém o campo `password`.

### 0.6 Tirar credenciais hardcoded dos serviços Python e do scraper
**Localização**: `recommendationApi/recommendation_api.py:23-29`, `rake_keywords/keywordExtraction.py:11-15`, `scrapper_events_ufsm/src/database/data-source.ts:9-16`.

**Problema**: `host: "localhost"`, `password: "1234"` hardcoded nos três lugares — inconsistente com o backend Java, que já usa `${DB_PASSWORD:...}`.

**Ação**: Trocar por leitura de variáveis de ambiente (`os.environ["DB_PASSWORD"]` no Python, `process.env.DB_PASSWORD` no TypeScript), com um `.env`/`.env.example` por serviço.

**Critério de aceite**: `grep -rn '"1234"'` e `grep -rn "localhost"` dentro do código-fonte (não em comentários/README) não retorna credenciais literais.

---

## FASE 1 — Backend Spring Boot (eventosUfsm)

### 1.1 CORS
**Problema**: `@CrossOrigin(origins = "*")` em todos os controllers.
**Ação**: Configurar CORS centralizado no `SecurityConfig` com uma allowlist de origens (domínio de produção do front + `localhost` em dev), e remover as anotações `@CrossOrigin(origins = "*")` espalhadas pelos controllers.

### 1.2 Migrations reais em vez de `ddl-auto=update`
**Problema**: `application.properties:10` usa `spring.jpa.hibernate.ddl-auto=update` — risco de drift de schema em produção. Existe um arquivo de migration (`src/main/resources/db.migration/V1__create-tables-events.sql`) mas Flyway não está no `pom.xml` e a pasta tem nome errado (deveria ser `db/migration`).
**Ação**:
1. Adicionar dependência `flyway-core` (+ `flyway-database-postgresql` se for Postgres) no `pom.xml`.
2. Mover/renomear migrations existentes para `src/main/resources/db/migration/`, seguindo convenção `V{n}__descricao.sql`.
3. Gerar migrations que representem o schema atual (via `hibernate.hbm2ddl.auto=validate` + diff, ou escrevendo manualmente a partir das entidades).
4. Trocar `ddl-auto` para `validate` em todos os ambientes.

### 1.3 Corrigir `FetchType.EAGER` em `User`
**Localização**: `model/user/User.java:62-75`.
**Problema**: coleções (bookmarks, comentários, avaliações, eventos criados, lembretes) carregadas `EAGER` — N+1 garantido toda vez que um `User` é carregado.
**Ação**: Trocar todas para `FetchType.LAZY`. Ajustar qualquer código que dependa do carregamento eager (usar `@EntityGraph` ou fetch join explícito nos poucos lugares que realmente precisam das coleções).

### 1.4 Testes automatizados
**Problema**: `src/test` não existe, apesar de `spring-boot-starter-test` e `spring-security-test` estarem no `pom.xml`. O CI roda `mvn clean install -DskipTests` explicitamente.
**Ação**:
1. Criar testes de integração (`@SpringBootTest` + `MockMvc` ou `WebTestClient`) cobrindo pelo menos: login/cadastro, autorização (usuário não pode mexer em dado de outro — valida a correção da 0.4), CRUD de eventos, recomendação.
2. Remover `-DskipTests` do `.gitlab-ci.yml` e falhar o build se os testes não passarem.

### 1.5 Padronizar tratamento de erros
**Problema**: try/catch ad hoc por controller retornando strings/status genéricos; sem `@ControllerAdvice` global.
**Ação**: Criar `GlobalExceptionHandler` com `@RestControllerAdvice`, mapeando exceções de domínio para status HTTP corretos e um corpo de erro padronizado (`{ "error": "...", "message": "..." }`).

### 1.6 Limpeza de código
- Trocar `System.out.println`/`printStackTrace` (achados em ~8 arquivos, incluindo `AuthService.generateSignInResponse` que imprime a resposta de login inteira no stdout) por SLF4J (`@Slf4j`).
- Remover a classe `Output` (logging colorido customizado) em favor de SLF4J em todo o projeto.
- Remover import comentado em `EventsService.java:10`.
- Renomear pacote `routes` → `controllers` e classes `*Route` → `*Controller` (opcional, mas alinha com convenção Spring; fazer só se não quebrar nada em massa desnecessariamente).
- Mover DTOs que hoje são records internos dos controllers (`AuthRoute.RegisterDTO`, `LoginDTO`, etc.) para um pacote `dto/` dedicado.

### 1.7 Perfis de ambiente
**Ação**: Criar `application-dev.properties` e `application-prod.properties`, sem valores default inseguros (`DB_PASSWORD:1234`) no perfil de produção — falhar a inicialização se a variável não estiver setada em prod.

---

## FASE 2 — Frontend mobile (eventosFront) — a maior frente de trabalho

Este é o núcleo do pedido do usuário: hoje é **um site Ionic testado só no navegador com uma casca Capacitor gerada uma vez e nunca configurada de verdade**. Tratar cada item abaixo como obrigatório para considerar o app "pronto para Android/iOS", não opcional.

### 2.1 Rebrand do projeto Capacitor
**Problema**: `capacitor.config.ts` tem `appId: 'io.ionic.starter'`, `appName: 'eventosFront'`. `android/app/build.gradle` e `strings.xml` também têm `io.ionic.starter`.
**Ação**:
1. Definir um `appId` real (formato reverse-domain, ex. `br.ufsm.eventos`) e um `appName` de exibição definitivo.
2. Atualizar `capacitor.config.ts`, `android/app/build.gradle` (`applicationId`, `namespace`), `strings.xml`, e rodar `npx cap sync` para propagar.
3. Gerar ícone de app e splash screen reais (não os padrões genéricos do Ionic) — usar `@capacitor/assets` para gerar todos os tamanhos a partir de uma arte-fonte, configurar `SplashScreen` no `capacitor.config.ts`.

**Critério de aceite**: build gerado não tem nenhuma referência a `io.ionic.starter`; ícone/splash são específicos do app, não os placeholders do CLI.

### 2.2 Remover boilerplate do template Ionic nunca finalizado
**Problema**: `src/app/tab2/`, `src/app/tab3/` são as abas de exemplo do CLI, nunca substituídas. A aba "Usuário" (`tabs.page.html`) navega para `/tabs/tab3`, que renderiza literalmente `<ion-title>Tab 3</ion-title>` e `<app-explore-container name="Tab 3 page">` — isso está indo para produção.
**Ação**: Implementar de verdade a página de perfil/usuário que a aba "Usuário" deveria mostrar (dados do usuário logado, preferências, logout, etc.), removendo `tab2`/`tab3`/`explore-container` do CLI. Remover também a rota comentada `/home` órfã em `app-routing.module.ts`.

### 2.3 Environment de produção quebrado
**Problema**: `src/environments/environment.prod.ts` tem `api: 'http://localhost:9090/api'` — idêntico ao dev. Um app rodando num celular chamando `localhost` sempre vai falhar. Também falta a chave `recommendationApi` presente em `environment.ts`, então a recomendação quebra em produção (`EventService.getEventsRecommended()` chama `undefined/<userId>`).
**Ação**:
1. Definir a URL pública real do backend (domínio/IP acessível pela internet, com HTTPS) em `environment.prod.ts`.
2. Adicionar a chave `recommendationApi` faltante em `environment.prod.ts`, apontando para o endpoint público do `recommendationApi` (atrás do backend Java como proxy, idealmente — ver Fase 4).
3. Confirmar que o backend está servindo em HTTPS antes de apontar produção pra ele (HTTP puro para uma API pública é inaceitável).

**Critério de aceite**: build de produção (`ionic build --prod` + `npx cap sync`) instalado num dispositivo físico consegue logar, listar eventos e ver recomendações sem nenhuma chamada a `localhost`.

### 2.4 Storage seguro de token (Capacitor Preferences / Secure Storage)
**Localização**: `src/app/service/auth.token.service.ts`, `src/app/service/auth-interceptor.service.ts`.
**Problema**: token guardado em `localStorage` puro — inseguro em WebView, sem criptografia. Além disso há um bug real: `TokenService.setTokens()` grava tudo como um blob JSON sob a chave `environment.tokenKey` (`'token_key'`), mas o interceptor lê `localStorage.getItem('accessToken')` (chave plana diferente) — o interceptor **nunca encontra o token**, e cada serviço ficou compensando isso manualmente re-buscando e anexando o token por fora.
**Ação**:
1. Trocar `localStorage` por `@capacitor/preferences` (ou Secure Storage, se disponível) para persistir o token.
2. Unificar em uma única chave/formato de leitura e escrita — eliminar a divergência `'accessToken'` vs `'token_key'`.
3. Fazer o `auth-interceptor.service.ts` funcionar de verdade (anexar `Authorization: Bearer <token>` a toda requisição autenticada), e então remover a lógica duplicada de anexar token manualmente em cada serviço (`EventService`, etc.) — o interceptor deve ser o único lugar que faz isso.

**Critério de aceite**: interceptor testado unitariamente (havia zero cobertura disso, o que permitiu o bug passar) garantindo que o header `Authorization` é anexado corretamente em requisições autenticadas.

### 2.5 Corrigir cálculo de expiração do JWT
**Localização**: `TokenService.tokenValid()`.
**Problema**: `decodeToken.exp * 2000` — `exp` do JWT é em segundos, deveria multiplicar por 1000 (não 2000). Isso dobra silenciosamente a validade percebida do token no client.
**Ação**: Corrigir para `* 1000`. Adicionar teste unitário cobrindo token expirado vs válido.

### 2.6 Guards de rota reais
**Localização**: `src/app/service/auth.guard.ts`.
**Problema**: `IsAdminGuard`/`AdminGuard` sempre retornam `true` e nem estão registrados em `app-routing.module.ts` — não há proteção de rota por papel no client.
**Ação**: Implementar a checagem real de papel (ler do token decodificado ou de um endpoint `/me`), aplicar os guards nas rotas administrativas, e negar acesso/redirecionar quando o usuário não tiver permissão. (Lembrar: isso é defesa em profundidade — a autorização real tem que estar no backend, Fase 0.4 — mas o guard evita expor UI que o usuário não deveria nem ver.)

### 2.7 Plugins nativos reais via Capacitor
**Problema**: dependências de Capacitor (`@capacitor/status-bar`, `@capacitor/keyboard`, `@capacitor/haptics`, `@capacitor/app`, `@capacitor/google-maps`) estão no `package.json` mas **nunca são usadas** — zero chamadas nativas no código.
**Ação, mínimo viável**:
1. **Status bar**: configurar cor/estilo consistente com o tema do app (`StatusBar.setStyle()`, `setBackgroundColor()`), especialmente por trás do notch em iOS.
2. **App state / back button**: usar `App.addListener('backButton', ...)` (ou `Platform.backButton` do Ionic) para tratar o botão físico de voltar do Android de forma coerente com a pilha de navegação — hoje não existe nenhum tratamento disso.
3. **Safe area**: usar as variáveis CSS `env(safe-area-inset-*)` do Ionic/Capacitor em `global.scss` para não deixar conteúdo atrás do notch/barra de gestos.
4. **Network**: adicionar `@capacitor/network` para detectar perda de conectividade e mostrar estado offline (ver 2.9).
5. Remover do `package.json` qualquer plugin que se decida definitivamente não usar (ex. se mapas continuarem via Leaflet, `@capacitor/google-maps` deve ser removido, não deixado morto).

### 2.8 Notificações push (se o produto precisar — confirmar com o time, mas a infraestrutura de lembretes já existe e pede isso)
**Problema**: a única integração "nativa" hoje é `reminders.page.ts` usando plugins Cordova **descontinuados** (`@awesome-cordova-plugins/local-notifications`, `android-permissions`), só no caminho Android (`platform.is('cordova')`), sem equivalente iOS, misturando Cordova num projeto Capacitor 6 — frágil, tende a quebrar no próximo `cap sync`.
**Ação**: Migrar `reminders.page.ts` para `@capacitor/local-notifications` (plugin Capacitor nativo, não Cordova), implementando o fluxo tanto para Android quanto iOS. Se push remoto (servidor → dispositivo) for necessário além de lembretes locais, adicionar `@capacitor/push-notifications` + integração com FCM/APNs — mas isso é um adicional, não bloqueante para "app funcional".

### 2.9 Estados de rede, loading, erro e cache básico
**Problema**: sem `Network` plugin, sem detecção de offline, sem retry/backoff, sem cache local. `home-page.page.ts.doRefresh()` usa `setTimeout(3000)` fixo em vez de completar quando a resposta real chega. Estados de erro majoritariamente só `console.error`, sem feedback visível ao usuário na maioria dos fluxos.
**Ação**:
1. Adicionar `@capacitor/network` e um serviço de conectividade que os componentes possam observar.
2. Corrigir `doRefresh()` para completar o `ion-refresher` no `complete()`/`error()` da chamada HTTP real, não em um timeout arbitrário.
3. Padronizar loading/erro/vazio: todo componente que busca dados deve ter os três estados visíveis (`ion-spinner` ou skeleton enquanto carrega, mensagem de erro com opção de retry, estado vazio quando não há dados) — hoje isso é inconsistente entre páginas.
4. Cache leve: manter a última lista de eventos carregada em memória/Preferences para exibir algo mesmo se a rede cair, com indicação de "dados podem estar desatualizados".

### 2.10 Ionic idiomático em vez de HTML/CSS genérico + navegação nativa
**Ação**:
1. Revisar CSS/SCSS em busca de larguras fixas em pixels e layouts pensados para desktop; substituir por componentes Ionic (`ion-grid`, `ion-col` com breakpoints) e unidades relativas.
2. Usar `Platform` do `@ionic/angular` para diferenciar comportamento iOS/Android onde fizer sentido (ex. ícones, confirmações, gestos), não só na página de lembretes.
3. Preferir `NavController` do Ionic sobre `Router.navigate` puro nos fluxos de navegação principal, para manter a pilha de navegação nativa (animações de transição, back consistente) coerente com o `backButton` handler da 2.7.
4. Remover `user-scalable=no` do viewport em `index.html` (problema de acessibilidade herdado do template, nunca reconsiderado).

### 2.11 Testes reais
**Problema**: todo `*.spec.ts` é o boilerplate padrão do Angular CLI (`expect(component).toBeTruthy()`), sem cobertura de lógica real — inclusive isso é por que o bug de token (2.4) nunca foi pego.
**Ação**: Escrever testes reais para: `TokenService` (token válido/expirado — cobre 2.5), `AuthInterceptor` (header anexado corretamente — cobre 2.4), guards de rota (2.6), e pelo menos os componentes de página mais usados (home, login, eventos).

### 2.12 Build e validação em dispositivo real
**Ação, ao final da Fase 2**:
1. `npx cap sync android` e `npx cap sync ios`, build de release para ambas plataformas.
2. Instalar em um dispositivo Android físico e (se houver Mac disponível) em um simulador/dispositivo iOS.
3. Percorrer os fluxos principais (cadastro, login, listar eventos, recomendação, bookmark, lembrete com notificação local, logout) verificando que nada depende de comportamento exclusivo de navegador (ex. `window.confirm`, `alert()` nativos do browser — trocar por `AlertController`/`ActionSheetController` do Ionic onde existirem).

**Critério de aceite da Fase 2 inteira**: o app instalado via APK/IPA de release funciona os fluxos principais fim-a-fim contra o backend de produção, sem nenhuma tela ou comportamento herdado do template Ionic padrão.

---

## FASE 3 — Microsserviços Python

### 3.1 `recommendationApi`
- **Autenticação**: hoje qualquer um pode pedir recomendação/explicação de qualquer `user_id` (`/recommendations/{user_id}`). Adicionar validação de que o `user_id` da URL corresponde ao usuário autenticado (via JWT vindo do backend Java, validado com o mesmo secret/chave pública, ou via um serviço interno só acessível pelo backend — não exposto direto à internet).
- **CORS**: trocar `allow_origins=["*"]` por uma allowlist (domínio do backend/frontend).
- **Cache em memória**: o dict `_cache` (linha ~32) não tem eviction além de TTL na leitura e não escala para múltiplos workers/réplicas. Se for rodar com mais de 1 worker, trocar por Redis ou aceitar explicitamente que é single-instance (documentar a limitação).
- **`requirements.txt` sem versões fixadas**: fixar todas as dependências (`fastapi==X.Y.Z`, etc.) para build reprodutível.
- **Deploy**: criar um `Dockerfile` (multi-stage, `uvicorn`/`gunicorn` como servidor, não dev server) e adicionar ao `docker-compose.yml` da raiz.
- **Testes**: adicionar testes de unidade para as funções de similaridade/collaborative filtering e um teste de integração do endpoint principal.

### 3.2 `rake_keywords`
- **Bug de execução no import**: `extract_and_store_keywords_for_events()` é chamada no nível do módulo (linha 91), sem `if __name__ == "__main__":` — importar o arquivo em qualquer lugar já dispara escrita no banco. Adicionar o guard `if __name__ == "__main__":`.
- **Nome enganoso**: o projeto se chama "rake_keywords" mas usa YAKE, não RAKE (`rake_nltk` está instalado mas nunca usado). Remover a dependência não usada; considerar renomear o projeto para refletir o algoritmo real (`yake_keywords` ou similar) — opcional, mas evita confusão futura.
- **Sem tratamento de erro**: nenhum try/except ao redor da conexão com Postgres ou da extração — adicionar tratamento e logging.
- **Scheduling**: hoje é um script one-shot sem agendamento. Decidir e implementar como/quando ele deve rodar (cron externo, ou um agendador simples tipo `APScheduler`, ou disparado pelo backend Java após o scraper rodar).
- **`requirements.txt` sem versões fixadas**: mesmo tratamento da 3.1.
- **Migrar credenciais para env vars**: já coberto na Fase 0.6.

### 3.3 Trazer os dois serviços para o monorepo de verdade
Ver Fase 5 (consolidação de git) — hoje nem `recommendationApi` nem `rake_keywords` estão versionados no repositório principal.

---

## FASE 4 — Scraper (scrapper_events_ufsm)

### 4.1 Consertar o entrypoint quebrado
**Problema**: o `Dockerfile` e o `docker-compose.yml` referenciam um `getEvents.ts` na raiz do projeto que **não existe** — o arquivo real está em `src/scrape/getEvents.ts`, e a função exportada nunca é chamada em lugar nenhum. `package.json`'s script `start` também aponta para um arquivo (`src/database/index.ts`) que não existe. **O scraper, como está configurado, não roda.**
**Ação**:
1. Adicionar um entrypoint real (`src/main.ts` ou similar) que importa e chama `getEventData()` (ou o pipeline completo de scraping).
2. Corrigir `Dockerfile` (`CMD`) e `docker-compose.yml` para apontar para esse entrypoint real.
3. Corrigir o script `start` do `package.json`.

**Critério de aceite**: `docker compose up` do serviço realmente executa uma raspagem de ponta a ponta e persiste no banco, sem erro de arquivo não encontrado.

### 4.2 Robustez do scraping
**Problema**: seletores CSS extremamente frágeis e posicionais (ex. `#arch-main-section > div:nth-child(5) > ul > li > div > div.col-lg-10.info-busca-lista`) sem fallback, quebram silenciosamente ou lançam exceção não tratada em parsing de data (`.split('/')` sem guard) se o HTML do site da UFSM mudar. Zero retry/backoff, zero rate-limiting — requisições sequenciais disparadas sem pausa contra o site da UFSM.
**Ação**:
1. Envolver toda chamada `axios.get` em try/catch com retry exponencial (2–3 tentativas) e log estruturado de falha (não travar o processo inteiro por uma página que falhou).
2. Adicionar uma pausa (100–500ms) entre requisições sequenciais e um `User-Agent` identificável.
3. Adicionar validação defensiva no parsing de data e nos seletores (se o seletor não encontrar nada, logar um aviso com a URL e pular o item, não lançar exceção não tratada).
4. Checar `robots.txt` do site alvo e respeitar `Disallow`/`Crawl-delay` se existirem.

### 4.3 Banco e configuração
**Problema**: `data-source.ts` tem `synchronize: false` e `migrations: []` — schema tem que existir por fora, sem documentação de como. Já coberto parcialmente pela Fase 0.6 (credenciais).
**Ação**: Adicionar migrations TypeORM reais (`npm run typeorm migration:generate`), documentar no README como aplicar o schema inicial.

### 4.4 Limpeza
- Remover dependências não usadas (`playwright`, `puppeteer`, `jsdom` — nada disso aparece no código real).
- Corrigir `.gitlab-ci.yml`: hoje faz login com `$CI_REGISTRY_USER/$CI_REGISTRY_PASSWORD` (credenciais do GitLab) mas faz push para `docker.io` (`DOCKER_REGISTRY`) — mismatch de registry/credenciais.
- Trocar `node:16` (EOL) por uma versão LTS atual no `Dockerfile`.
- Adicionar testes básicos (parsing de HTML de fixture local, não contra o site real).

---

## FASE 5 — Infraestrutura e consolidação do monorepo

### 5.1 Resolver o problema dos `.git` aninhados
**Problema**: `rake_keywords/` e `scrapper_events_ufsm/` têm seus próprios diretórios `.git` internos, mas **não há `.gitmodules`** — não são submodules de verdade, são apenas diretórios opacos que o git da raiz ignora (aparecem como `??`). Isso significa que um clone novo do repo principal **não traz o código desses dois serviços**, e não há histórico/CI/review ligado ao monorepo para eles. `recommendationApi` nem tem `.git` próprio ainda.
**Ação** — escolher uma estratégia e aplicar às três pastas (`rake_keywords`, `scrapper_events_ufsm`, `recommendationApi`):
- **Opção recomendada (mais simples)**: remover o `.git` interno de `rake_keywords` e `scrapper_events_ufsm` (preservando o código, só descartando o histórico duplicado — ou migrando o histórico relevante para o monorepo antes, se ele importar) e adicionar as três pastas como diretórios normais rastreados pelo git raiz.
- **Opção alternativa**: formalizar como git submodules de verdade (`git submodule add` apontando para os repositórios remotos existentes), se houver razão para mantê-los como repositórios independentes (ex. equipes diferentes, ciclo de release separado).
**Critério de aceite**: um `git clone` novo do repositório raiz traz o código-fonte completo de todos os cinco serviços sem passos manuais extras.

### 5.2 Unificar `docker-compose.yml`
**Problema**: existem três arquivos `docker-compose.yml` divergentes (raiz, `eventosUfsm/`, `scrapper_events_ufsm/`), com nomenclatura de container e portas diferentes — nenhuma documentação de qual é o "oficial". `eventosUfsm/docker-compose.yml` tem inversão de porta (`"5432:5433"`, host:container trocados) e todos hardcodam `POSTGRES_PASSWORD: 1234` em vez de usar `env_file`.
**Ação**:
1. Manter **um único** `docker-compose.yml` na raiz, orquestrando os cinco serviços (backend, frontend — se fizer sentido conteinerizar o front para dev —, banco, `recommendationApi`, `rake_keywords`, scraper) com uma rede compartilhada.
2. Remover os `docker-compose.yml` duplicados de `eventosUfsm/` e `scrapper_events_ufsm/` (ou deixá-los claramente marcados como "uso standalone/dev apenas", se houver razão para mantê-los).
3. Trocar toda credencial hardcoded em YAML por `env_file: .env` referenciando os `.env.example`/`.env` corrigidos na Fase 0.
4. Corrigir o mapeamento de porta invertido do Postgres.

### 5.3 CI/CD
**Problema**: `eventosUfsm/.gitlab-ci.yml` e `scrapper_events_ufsm/.gitlab-ci.yml` só fazem build → push → `docker run` via SSH direto (sem parar/remover container antigo antes do redeploy, sem rollback, sem healthcheck). `frontend`, `rake_keywords` e `recommendationApi` não têm CI nenhum. Testes nunca rodam (`-DskipTests` no Maven).
**Ação**:
1. Adicionar `.gitlab-ci.yml` para `eventosFront`, `recommendationApi` e `rake_keywords` (build + lint + testes, no mínimo).
2. Fazer o pipeline do backend rodar os testes da Fase 1.4 de verdade (remover `-DskipTests`).
3. Trocar o deploy via `docker run` direto por `docker compose pull && docker compose up -d` (usando o compose unificado da 5.2) no servidor, garantindo que containers antigos são parados/substituídos corretamente.
4. Adicionar um estágio de scan de dependências (`npm audit`, `mvn dependency-check`, `pip-audit`) — não precisa bloquear o pipeline inicialmente, mas precisa reportar.

### 5.4 Dockerfiles
- `eventosUfsm/Dockerfile`: builder usa Java 17, imagem final usa `openjdk:22-jdk-slim` — alinhar as duas para a mesma versão (17, se for o target do `pom.xml`).
- `scrapper_events_ufsm/Dockerfile`: trocar `node:16` (EOL) por uma LTS atual, usar build multi-stage para não carregar `devDependencies` na imagem final.

---

## Ordem de execução recomendada

1. **Fase 0** (segurança) — sempre primeiro, é bloqueante e barato de fazer.
2. **Fase 1** (backend) — a Fase 2.3/2.4 do frontend depende de o backend já estar seguro e com HTTPS/URL pública definida.
3. **Fase 5.1 e 5.2** (consolidar monorepo e compose) — fazer antes das Fases 3 e 4 para já trabalhar nos serviços dentro da estrutura final.
4. **Fase 3 e 4** (Python + scraper) — podem ser paralelizadas entre si.
5. **Fase 2** (frontend mobile) — é a maior fase; fazer por último garante que já existe uma API de produção real (URL pública, HTTPS, autenticação corrigida) para apontar e testar em dispositivo físico de verdade, em vez de mockar contra `localhost`.
6. **Fase 5.3 e 5.4** (CI/CD, Dockerfiles) — podem ser feitas em paralelo com qualquer fase acima, mas fazem mais sentido depois que os testes (Fase 1.4, 2.11) existirem para o CI rodar algo de fato.

## Não-objetivos (fora de escopo deste plano)

- Reescrever a lógica de recomendação (TF-IDF + collaborative filtering) — ela é funcionalmente sólida, só precisa de segurança/deploy.
- Adicionar novas features de produto não mencionadas aqui.
- Migrar de Angular/Ionic para outro framework — o objetivo é fazer o Ionic/Capacitor existente funcionar de verdade em mobile, não trocar de stack.
