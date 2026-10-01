CREATE TABLE inspection
(
    inspection_id   VARCHAR(50) PRIMARY KEY,
    vehicle_id      VARCHAR(100) NOT NULL,
    inspection_date TIMESTAMP    NOT NULL,
    status          VARCHAR(30)  NOT NULL
);

CREATE INDEX idx_inspection_vehicle_id
    ON inspection (vehicle_id);