CREATE TABLE vehicle_production (
    vehicle_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    current_stage VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ
);
