ALTER TABLE vehicle_production
    ADD production_line VARCHAR(255);

ALTER TABLE vehicle_production
    ADD started_event_id VARCHAR(255);

DROP TABLE flyway_bootstrap CASCADE;

ALTER TABLE vehicle_production
ALTER
COLUMN current_stage TYPE VARCHAR(255) USING (current_stage::VARCHAR(255));

ALTER TABLE vehicle_production
DROP
COLUMN order_id;

ALTER TABLE vehicle_production
DROP
COLUMN vehicle_id;

ALTER TABLE vehicle_production
    ADD order_id VARCHAR(255) NOT NULL;

ALTER TABLE vehicle_production
ALTER
COLUMN status TYPE VARCHAR(255) USING (status::VARCHAR(255));

ALTER TABLE vehicle_production
    ADD vehicle_id VARCHAR(255) NOT NULL PRIMARY KEY;