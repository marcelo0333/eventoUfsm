#!/usr/bin/env bash
# Build + deploy do eventosUfsm no Cloud Run, dentro da camada Always Free.
#
# Pré-requisitos: gcloud CLI autenticado (gcloud auth login), projeto com
# billing habilitado (o Always Free exige uma conta de billing associada,
# mesmo que o uso fique a custo zero) e os segredos já criados no Secret
# Manager (ver README-DEPLOY.md).
#
# Uso:
#   PROJECT_ID=meu-projeto ./deploy.sh
#   PROJECT_ID=meu-projeto REGION=us-east1 SERVICE_NAME=eventos-api ./deploy.sh

set -euo pipefail

PROJECT_ID="${PROJECT_ID:?defina PROJECT_ID, ex: PROJECT_ID=meu-projeto ./deploy.sh}"

# us-central1/us-east1/us-west1 são as ÚNICAS regiões cuja cota do Cloud Run
# entra no Always Free. Qualquer outra região (inclusive southamerica-east1)
# é cobrada desde o primeiro request. Trade-off: mais latência a partir do
# Brasil em troca de custo zero garantido.
REGION="${REGION:-us-central1}"
SERVICE_NAME="${SERVICE_NAME:-eventos-ufsm-api}"
IMAGE="gcr.io/${PROJECT_ID}/${SERVICE_NAME}"

echo "==> Build da imagem via Cloud Build (projeto=${PROJECT_ID})"
gcloud builds submit --tag "${IMAGE}" --project "${PROJECT_ID}" .

echo "==> Deploy no Cloud Run (região=${REGION}, serviço=${SERVICE_NAME})"
gcloud run deploy "${SERVICE_NAME}" \
  --image "${IMAGE}" \
  --project "${PROJECT_ID}" \
  --region "${REGION}" \
  --platform managed \
  --allow-unauthenticated \
  --port=8080 \
  --min-instances=0 \
  --max-instances=3 \
  --memory=512Mi \
  --cpu=1 \
  --cpu-boost \
  --set-env-vars="DB_URL=${DB_URL:?defina DB_URL},DB_USER=${DB_USER:?defina DB_USER},CORS_ALLOWED_ORIGINS=${CORS_ALLOWED_ORIGINS:?defina CORS_ALLOWED_ORIGINS com o(s) domínio(s) real(is) do frontend}" \
  --set-secrets="DB_PASSWORD=eventos-db-password:latest,JWT_SECRET=eventos-jwt-secret:latest"
  # --min-instances=0      -> nenhuma instância fica de pé sem tráfego = sem custo parado (scale-to-zero).
  # --max-instances=3      -> teto baixo: limita o dano se houver tráfego anômalo/abuso, o free tier
  #                            cobre 2M requisições/mês mas não protege contra pico repentino de custo.
  # --memory=512Mi         -> Spring Boot 3 + JVM roda confortável em 512Mi; a cota grátis é de
  #                            360.000 GiB-s/mês, então quanto menor a memória, mais requisições grátis cabem.
  # --cpu=1                -> menor incremento faturável de CPU no Cloud Run; suficiente para uma API
  #                            de tráfego baixo/médio.
  # --cpu-boost            -> CPU extra só durante o startup (não durante requests), reduz o cold start
  #                            da JVM sem custo adicional fora da janela de boot.
  # (propositalmente SEM --no-cpu-throttling: mantendo o throttling padrão, a CPU só é cobrada durante
  #  o processamento de requests, que é o que mantém o serviço dentro do free tier em repouso.)

echo "==> URL do serviço:"
gcloud run services describe "${SERVICE_NAME}" \
  --region "${REGION}" \
  --project "${PROJECT_ID}" \
  --format='value(status.url)'
