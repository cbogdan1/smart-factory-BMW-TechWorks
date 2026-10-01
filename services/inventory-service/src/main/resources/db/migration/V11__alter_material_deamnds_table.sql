ALTER TABLE material_demands ADD COLUMN IF NOT EXISTS id_uuid UUID DEFAULT gen_random_uuid();
ALTER TABLE material_demands DROP CONSTRAINT IF EXISTS material_demands_pkey;
ALTER TABLE material_demands DROP COLUMN IF EXISTS id;
ALTER TABLE material_demands RENAME COLUMN id_uuid TO id;
ALTER TABLE material_demands ADD PRIMARY KEY (id);

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='material_demands' AND column_name='event_id') THEN
ALTER TABLE material_demands RENAME COLUMN event_id TO demand_id;
END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='material_demands' AND column_name='planned_start_date') THEN
ALTER TABLE material_demands RENAME COLUMN planned_start_date TO planned_date;
END IF;
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='material_demands' AND column_name='created_at') THEN
ALTER TABLE material_demands RENAME COLUMN created_at TO created_date;
END IF;
END $$;

ALTER TABLE material_demands DROP COLUMN IF EXISTS order_id;
ALTER TABLE material_demands DROP COLUMN IF EXISTS production_line;
ALTER TABLE material_demands DROP COLUMN IF EXISTS priority;

DROP INDEX IF EXISTS idx_material_demands_planned_start_date;
CREATE INDEX IF NOT EXISTS idx_material_demands_planned_date ON material_demands(planned_date);