CREATE TABLE vehicle_twin
(
    vehicle_id       VARCHAR(255) NOT NULL,
    order_id         VARCHAR(255),
    vehicle_model    VARCHAR(255),
    status           VARCHAR(255),
    current_stage    VARCHAR(255),
    current_location VARCHAR(255),
    quality_status   VARCHAR(255),
    rework_count     INTEGER,
    last_updated     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_vehicle_twin PRIMARY KEY (vehicle_id)
);