CREATE TABLE reservations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id VARCHAR(50) NOT NULL UNIQUE,
    demand_id UUID NOT NULL,
    part_id UUID NOT NULL,
    quantity_RESERVED BIGINT NOT NULL,
    reserved_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT fk_reservations_demand FOREIGN KEY (demand_id) REFERENCES material_demands(id) ON DELETE CASCADE,
    CONSTRAINT  fk_reservations_part FOREIGN KEY (part_id) REFERENCES parts(id) ON DELETE RESTRICT,

    CONSTRAINT chk_reservations_quantity CHECK (quantity_RESERVED > 0),
    CONSTRAINT chk_reservations_status CHECK (status IN ('ACTIVE', 'RELEASED', 'CANCELLED'))

);
CREATE INDEX idx_reservations_reservation_id ON reservations(reservation_id);
CREATE INDEX idx_reservations_demand_id ON reservations(demand_id);
CREATE INDEX idx_reservations_part_id ON reservations(part_id);