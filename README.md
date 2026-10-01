# Smart Factory Platform

Event-driven microservice platform (8 services) communicating over Apache Kafka, each owning a
private PostgreSQL database. 

> 🎬 **Running the demo?** Follow
> **[docs/12-demo-runbook-3-flows.md](docs/12-demo-runbook-3-flows.md)** — a click-by-click
> Swagger UI + Kafka UI walkthrough of the happy-path, rework and failed flows.

## Layout

```
smart-factory/
├── pom.xml                      # Maven parent (aggregator + dependency/plugin management)
├── docker-compose.yaml          # shared infra (Kafka + Postgres + Kafka UI) + 8 services
├── init-db/create-databases.sql # creates the 8 service databases
├── infra/create-topics.sh       # (optional) explicit Kafka topic creation
├── services/                    # the 8 microservice modules (skeletons)
│   ├── order-service/           # Team 1  · orderdb       · dev :8081
│   ├── planning-service/        # Team 2  · planningdb    · dev :8082
│   ├── inventory-service/       # Team 3  · inventorydb   · dev :8083
│   ├── procurement-service/     # Team 4  · procurementdb · dev :8084
│   ├── assembly-service/        # Team 5  · assemblydb    · dev :8085
│   ├── agv-service/             # Team 6  · agvdb         · dev :8086
│   ├── quality-service/         # Team 7  · qualitydb     · dev :8087
│   └── dwh-service/             # Team 8  · dwhdb         · dev :8088
```

Each service is a standalone Quarkus app (REST + Panache + Kafka + Flyway + OpenAPI + health) and today
contains only a minimal `PingResource` (a Boundary component) — teams add entities, messaging, and
domain services per the docs. All modules inherit versions, the Quarkus BOM, and plugin config from
the root `pom.xml`.

## Architecture (ECB)

Every service follows the **Entity–Control–Boundary** pattern, one package per layer:

```
com.smartfactory.<svc>/
├── boundary/   # REST resources + Kafka adapters (the edge)   ── depends on control, entity
├── control/    # use cases, business logic, @Transactional     ── depends on entity
└── entity/     # Panache entities + repositories (owns schema) ── depends on nothing
```

Dependencies point inward (`boundary → control → entity`); `control` must never import
`boundary`. See **[docs/teams/ecb-architecture.md](docs/teams/ecb-architecture.md)** for the
layer rules and a full worked example.

## Database schema (Flyway)

Each service owns its schema via Flyway migrations in `src/main/resources/db/migration`
(`V1__init.sql`, `V2__…`, …). Migrations run automatically on startup
(`quarkus.flyway.migrate-at-start=true`) and Flyway creates its `flyway_schema_history`
table on first run. Because services run with `quarkus.hibernate-orm.database.generation=validate`,
**any entity change must be paired with a new migration** or startup fails.

> Local dev connects to the compose Postgres on `localhost:5432`, so start it first
> (`docker compose up -d postgres`) before `mvn quarkus:dev`.

## Prerequisites

| Tool | Version |
|------|---------|
| JDK | 21 |
| Maven | 3.9+ |
| Docker + Compose | recent |

## Building

```bash
# Build every module from the repo root (reactor)
mvn clean package

# Build a single module (with its parent)
mvn -pl services/order-service -am clean package
```

## Quick start

```bash
# 1) Start shared infrastructure
docker compose up -d kafka postgres kafka-ui
#    Kafka UI: http://localhost:8080 · Postgres: localhost:5432 (factory/factory)
#    On first start this also runs automatically:
#      • create-databases.sql  → creates the 8 service databases (Postgres init)
#      • create-topics.sh       → creates the 17 topics (one-shot `kafka-init` service)
#    Re-run topic creation anytime with: docker compose up kafka-init

# 2) Run a service in dev mode (hot reload)
cd services/order-service
mvn quarkus:dev
#    Health:     http://localhost:8081/q/health
#    Swagger UI: http://localhost:8081/q/swagger-ui
#    Ping:       http://localhost:8081/api/ping

# 3) Build & run a service in a container
docker compose up -d --build order-service   # exposed on http://localhost:8081
```

> **Note:** the database and topic bootstrap scripts run only on first
> initialization. `create-databases.sql` runs when the `sf-pgdata` volume is
> empty — to reset it, `docker compose down -v`. Topic creation is idempotent
> (`--if-not-exists`) and can be re-run via `docker compose up kafka-init`.

"# smart-factory" 
