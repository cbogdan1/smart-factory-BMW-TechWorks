ALTER TABLE production_plan
    ADD COLUMN production_line VARCHAR(100);

ALTER TABLE production_plan
    ADD COLUMN priority VARCHAR(20);

ALTER TABLE production_plan
    ADD COLUMN planned_start_date DATE;