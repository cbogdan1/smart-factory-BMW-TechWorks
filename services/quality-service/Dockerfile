# Build from the repository root context so the Maven parent pom and shared
# `common` module are available.
#   docker compose build quality-service
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
# Poms first (better layer caching)
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY services/quality-service/pom.xml ./services/quality-service/pom.xml
RUN mvn -q -B -pl services/quality-service -am dependency:go-offline || true
# Sources (shared module + service), then build just this service + its deps
COPY common/src ./common/src
COPY services/quality-service/src ./services/quality-service/src
RUN mvn -q -B -pl services/quality-service -am -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/services/quality-service/target/quarkus-app/ /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
