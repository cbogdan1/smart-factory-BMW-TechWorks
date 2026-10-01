ALTER TABLE production_plan
    ALTER COLUMN plan_id DROP DEFAULT;

DROP SEQUENCE IF EXISTS production_plan_plan_id_seq;

ALTER TABLE production_plan
    ALTER COLUMN plan_id TYPE VARCHAR(255);

ALTER TABLE production_plan
    ALTER COLUMN order_id TYPE VARCHAR(255);

ALTER TABLE production_plan
    ALTER COLUMN vehicle_id TYPE VARCHAR(255);