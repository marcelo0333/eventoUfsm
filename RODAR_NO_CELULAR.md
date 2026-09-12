# Como rodar o SmartEventos no celular (Android)

Este guia leva o app pra rodar num **celular Android físico** via Android Studio,
usando um backend (Spring + Postgres) rodando localmente via Docker.

---

## O que já está configurado no projeto

- `AndroidManifest.xml`: HTTP "cleartext" liberado, permissões de notificação (Android 13+) e internet.
- `MainActivity.java`: mixed content liberado no WebView (a página do app roda em `https://localhost`,
  mas a API é HTTP puro na rede local — sem isso o WebView bloqueia a chamada).
- `SecurityConfig.java` (backend): CORS já libera a origem que o Capacitor usa no Android (`https://localhost`).
- Splash screen com a logo do app e tokens JWT com validade maior (24h / refresh de 30 dias),
  pra não precisar logar de novo toda hora.
- `docker-compose.yml`: o **scraper roda em loop automaticamente** (1 em 1h) assim que sobe,
  buscando eventos novos no site da UFSM — não precisa disparar manualmente.

Você só precisa ajustar o **IP da sua máquina** (abaixo) e seguir os passos.

---

## Pré-requisitos

1. **Docker Desktop** instalado e rodando (no Windows, precisa do WSL2 — o próprio instalador
   do Docker Desktop guia essa parte na primeira abertura).
2. **Node.js** (LTS) instalado — necessário pra buildar o front Angular.
3. **Android Studio** instalado (já traz um JDK compatível — o JBR).
4. **Celular Android físico** com **Depuração USB ativada**: Configurações → Sobre o telefone →
   toque 7x em "Número da versão" → volte → Opções do desenvolvedor → ative "Depuração USB".
   Conecte por USB com um **cabo de dados** (alguns cabos só carregam, não transferem dados).
5. **Celular e PC na MESMA rede Wi-Fi/LAN.**

---

## Passo 1 — Descobrir o IP da sua máquina e configurar

No celular, `localhost` aponta pro próprio aparelho — por isso o app precisa do IP da sua
máquina na rede local (LAN), não `localhost`.

```sh
ipconfig
# procure "Endereço IPv4" da sua rede Wi-Fi/Ethernet, ex: 192.168.0.42
```

Edite `eventosFront/src/environments/environment.ts` e troque o valor de `DEV_HOST` pelo seu IP:

```ts
const DEV_HOST = '192.168.0.42'; // seu IP aqui
```

> Se o IP da sua máquina mudar depois (trocou de rede), é só editar de novo e refazer o
> "Rebuild" do Passo 4.

---

## Passo 2 — Subir o backend

Na raiz do projeto (onde está o `docker-compose.yml`):

```sh
# (Opcional) copie o exemplo de variáveis de ambiente — os defaults já funcionam out-of-the-box
cp .env.example .env

# Sobe banco + backend + scraper (o scraper já começa a popular eventos automaticamente também a API de recomendação
docker compose up -d db backend scraper recommendation
```

O scraper demora alguns segundos pra rodar a primeira vez. Pra conferir se já populou eventos:

```sh
docker logs eventos_scraper --tail 20
```

Pra extrair as palavras-chave dos eventos (usadas nas recomendações), rode uma vez, depois
que o scraper já tiver inserido eventos:

```sh
docker compose --profile tools up rake
```

**Importante — Firewall do Windows**: na primeira vez que o backend subir, o Windows pode
perguntar se permite acesso à rede. **Permita** (redes privadas), senão o celular não alcança
a porta 9090. Pra testar: abra `http://SEU-IP:9090/api/events` no navegador do PC — se abrir
aí mas não no celular, é firewall ou os dois não estão na mesma rede.

> A API de recomendação (porta 8001) e o `rake` (palavras-chave) são **opcionais** — sem eles
> a home do app continua funcionando, só a seção "Recomendados" fica vazia.

---

## Passo 3 — Instalar as dependências do front

```sh
cd eventosFront
npm install
```

---

## Passo 4 — Build e sincronização com o Android

```sh
cd eventosFront

# 1. Build do web (usa o environment.ts com o seu IP)
node node_modules/@angular/cli/bin/ng.js build --configuration=development

# 2. Copia o web pro projeto Android + registra os plugins Capacitor
node node_modules/@capacitor/cli/bin/capacitor sync android
```

---

## Passo 5 — Abrir no Android Studio e rodar

1. Abra o **Android Studio** → **Open** → selecione a pasta **`eventosFront/android`**
   (⚠️ a pasta `android`, não a raiz do projeto).
2. Espere o **Gradle Sync** terminar (barra de progresso embaixo). Na primeira vez baixa dependências.
3. No topo, selecione seu **celular** no seletor de dispositivos (aparece quando conectado
   com depuração USB — aceite o prompt "Permitir depuração?" que aparece no celular).
4. Clique em **Run ▶** (Shift+F10).
5. O app **SmartEventos** instala e abre no celular.

---

## Alternativa — buildar e instalar o APK via linha de comando

```sh
cd eventosFront/android

# Use JDK 17 — o Gradle desse projeto não suporta versões mais novas.
# No Android Studio isso não é problema (ele usa o JDK embutido/JBR automaticamente).
JAVA_HOME="/caminho/para/jdk-17" ./gradlew assembleDebug

adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Primeiro uso

Sem conta ainda? Toque em **"Cadastre-se aqui"** na tela de login pra criar uma.

---

## Problemas comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| App abre mas login dá erro de rede | Backend fora, IP errado, ou firewall | Confirme `http://SEU-IP:9090/api/events` abrindo no PC; libere o firewall; confira o IP com `ipconfig`; confirme que editou `DEV_HOST` e refez o build (Passo 4) |
| Celular não aparece no Android Studio / adb | Depuração USB desligada, driver, ou cabo só de carga | Ative depuração USB; troque de cabo/porta USB (evite hubs); aceite o prompt de depuração no celular |
| Home sem eventos | Scraper ainda não rodou ou falhou | `docker logs eventos_scraper` — deve mostrar eventos inseridos; espere alguns segundos após o `docker compose up` |
| "Recomendados" vazio | API de recomendação não está no ar, ou `rake` não rodou | Suba `recommendation` e rode `docker compose --profile tools up rake` depois que houver eventos |
| Mapa cai sempre no mesmo lugar | Geocoding não resolveu o endereço | Esperado para endereços muito ruins; o ideal é o backend salvar lat/long (ver REFACTOR_PLAN) |
| IP mudou (trocou de rede) | `environment.ts` desatualizado | Edite `DEV_HOST` e refaça o Passo 4 |
