CREATE TABLE event_store
(
    id             BIGSERIAL PRIMARY KEY,
    event_id       VARCHAR(255) NOT NULL,
    event_type     VARCHAR(255) NOT NULL,
    correlation_id VARCHAR(255),
    source_service VARCHAR(255),
    timestamp      TIMESTAMPTZ,
    payload        JSONB,
    processed_at   TIMESTAMPTZ
);

CREATE UNIQUE
    INDEX uk_event_store_event_id
    ON event_store (event_id);