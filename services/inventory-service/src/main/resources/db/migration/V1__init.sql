-- Flyway baseline migration for inventory-service.
-- Dummy placeholder to initialize the schema history table.
-- Add real schema changes in later versions (V2__..., V3__..., etc.).

CREATE TABLE IF NOT EXISTS flyway_bootstrap (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    service_name VARCHAR(64)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

INSERT INTO flyway_bootstrap (service_name) VALUES ('inventory-service');

