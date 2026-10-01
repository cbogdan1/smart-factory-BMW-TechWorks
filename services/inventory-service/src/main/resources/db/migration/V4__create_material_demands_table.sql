CREATE TABLE material_demands (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) NOT NULL UNIQUE,
    plan_id            VARCHAR(255) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    vehicle_id VARCHAR(255) NOT NULL,
    production_line    VARCHAR(255),
    priority           VARCHAR(50),
    planned_start_date DATE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_material_demands_event_id ON material_demands(event_id);