ALTER TABLE vehicle_production
    ALTER COLUMN started_at DROP NOT NULL;

ALTER TABLE vehicle_production
    ADD CONSTRAINT uk_vehicle_production_started_event_id
        UNIQUE (started_event_id);
