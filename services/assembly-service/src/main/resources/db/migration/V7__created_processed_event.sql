CREATE TABLE event_processed
(
    event_id   VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT pk_event_processed PRIMARY KEY (event_id)
);