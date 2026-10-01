CREATE TABLE IF NOT EXISTS processed_event
(
    event_id     VARCHAR(100) PRIMARY KEY,
    event_type   VARCHAR(100) NOT NULL,
    processed_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS delivery_mission
(
    id                        BIGSERIAL PRIMARY KEY,
    mission_id                VARCHAR(100)  NOT NULL UNIQUE,
    material_request_event_id VARCHAR(100)  NOT NULL UNIQUE,
    request_id                VARCHAR(100)  NOT NULL,
    vehicle_id                VARCHAR(100)  NOT NULL,
    order_id                  VARCHAR(100)  NOT NULL,
    material                  VARCHAR(100)  NOT NULL,
    quantity                  INT           NOT NULL,
    source_node_id            VARCHAR(100)  NOT NULL,
    target_node_id            VARCHAR(100)  NOT NULL,
    planned_route             VARCHAR(2000) NOT NULL,
    distance_meters           INT           NOT NULL,
    status                    VARCHAR(50)   NOT NULL,
    created_date              TIMESTAMP     NOT NULL,
    completed_date            TIMESTAMP
);