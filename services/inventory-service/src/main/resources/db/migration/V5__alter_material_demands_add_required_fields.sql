ALTER TABLE material_demands
    ADD COLUMN required_part_id BIGINT NOT NULL DEFAULT 1;


ALTER TABLE material_demands
    ADD COLUMN required_quantity BIGINT NOT NULL DEFAULT 10;


ALTER TABLE material_demands
    ADD CONSTRAINT chk_material_demand_required_quantity
        CHECK (required_quantity > 0);


CREATE INDEX idx_material_demands_vehicle_id
    ON material_demands(vehicle_id);


CREATE INDEX idx_material_demands_planned_start_date
    ON material_demands(planned_start_date);
