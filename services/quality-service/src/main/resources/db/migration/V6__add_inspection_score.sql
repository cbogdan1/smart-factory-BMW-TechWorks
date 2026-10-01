-- The inspection now stores the quality score computed from the defects linked to it.
-- Both columns are nullable: NULL means "the score has never been calculated for this inspection".
ALTER TABLE inspection
    ADD COLUMN score INTEGER;

ALTER TABLE inspection
    ADD COLUMN calculated_at TIMESTAMP;
