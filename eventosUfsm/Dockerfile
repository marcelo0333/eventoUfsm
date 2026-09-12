# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Camada de dependências isolada do código-fonte: o cache do Docker só é
# invalidado (e o download do Maven só reexecuta) quando o pom.xml muda.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app
COPY --from=builder /app/target/eventosUfsm-0.0.1-SNAPSHOT.jar /app/eventosUfsm.jar

# app.image-directory default (/tmp/images, ver application.properties) precisa
# existir e ser gravável pelo usuário non-root.
RUN mkdir -p /tmp/images && chown -R app:app /app /tmp/images
USER app

# Cloud Run injeta PORT (default 8080 se não definido); a app lê via
# server.port=${SERVER_PORT:${PORT:8080}} em application.properties.
EXPOSE 8080

# MaxRAMPercentage em vez de -Xmx fixo: a JVM calcula o heap como % da memória
# do container (JDK 17 detecta o cgroup automaticamente), então o mesmo jar se
# adapta a qualquer --memory configurado no Cloud Run sem rebuild.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/eventosUfsm.jar"]
