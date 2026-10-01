-- V15__alter_material_demand_for_vehicle_bom.sql


-- Remove old required_part_id relationship
ALTER TABLE material_demands
DROP CONSTRAINT IF EXISTS fk_material_demands_required_part;


ALTER TABLE material_demands
DROP COLUMN IF EXISTS required_part_id;


-- Add part_code instead
ALTER TABLE material_demands
    ADD COLUMN part_code VARCHAR(255);


-- Add vehicle model
ALTER TABLE material_demands
    ADD COLUMN IF NOT EXISTS vehicle_model VARCHAR(255);


-- Existing rows need a value
UPDATE material_demands
SET part_code = 'UNKNOWN'
WHERE part_code IS NULL;


UPDATE material_demands
SET vehicle_model = 'UNKNOWN'
WHERE vehicle_model IS NULL;


-- Make mandatory
ALTER TABLE material_demands
    ALTER COLUMN part_code SET NOT NULL;


ALTER TABLE material_demands
    ALTER COLUMN vehicle_model SET NOT NULL;


-- Change quantity to INTEGER
ALTER TABLE material_demands
ALTER COLUMN required_quantity TYPE INTEGER;


ALTER TABLE material_demands
    ALTER COLUMN required_quantity DROP DEFAULT;


-- Required quantity must be positive
ALTER TABLE material_demands
DROP CONSTRAINT IF EXISTS chk_material_demand_required_quantity;


ALTER TABLE material_demands
    ADD CONSTRAINT chk_material_demand_required_quantity
        CHECK (required_quantity > 0);


-- part_code references parts.part_code
ALTER TABLE material_demands
    ADD CONSTRAINT fk_material_demands_part_code
        FOREIGN KEY (part_code)
            REFERENCES parts(part_code);


-- Indexes
CREATE INDEX IF NOT EXISTS idx_material_demands_part_code
    ON material_demands(part_code);


CREATE INDEX IF NOT EXISTS idx_material_demands_planned_date
    ON material_demands(planned_date);


CREATE INDEX IF NOT EXISTS idx_material_demands_vehicle_id
    ON material_demands(vehicle_id);


CREATE INDEX IF NOT EXISTS idx_material_demands_plan_id
    ON material_demands(plan_id);

ALTER TABLE material_demands
    ADD COLUMN event_id VARCHAR(255) NOT NULL;
