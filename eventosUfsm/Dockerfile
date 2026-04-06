FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src /app/src

RUN mvn clean package -DskipTests

FROM openjdk:22-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/eventosUfsm-0.0.1-SNAPSHOT.jar /app/eventosUfsm.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "eventosUfsm.jar"]
