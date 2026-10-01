ALTER TABLE inspection
    ADD COLUMN decision VARCHAR(20);

ALTER TABLE inspection
    ADD COLUMN target_stage VARCHAR(30);

ALTER TABLE inspection
    ADD COLUMN decided_at TIMESTAMP;