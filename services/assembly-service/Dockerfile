# Build from the repository root context so the Maven parent pom and shared
# `common` module are available.
#   docker compose build assembly-service
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
# Poms first (better layer caching)
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY services/assembly-service/pom.xml ./services/assembly-service/pom.xml
RUN mvn -q -B -pl services/assembly-service -am dependency:go-offline || true
# Sources (shared module + service), then build just this service + its deps
COPY common/src ./common/src
COPY services/assembly-service/src ./services/assembly-service/src
RUN mvn -q -B -pl services/assembly-service -am -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/services/assembly-service/target/quarkus-app/ /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
