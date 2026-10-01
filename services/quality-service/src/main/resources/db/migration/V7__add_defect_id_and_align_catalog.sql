-- Aligns the standard defect catalog seeded in V4 with the official quality catalog:
-- adds the defect_id identifier and corrects the descriptions and penalty points.
-- V4 is already applied on every environment, so its checksum must not change - everything
-- happens here instead. defect_id is a unique identifier; code remains the primary key.

-- The rows already exist, so the column is added first and filled in afterwards.
ALTER TABLE defect
    ADD COLUMN defect_id VARCHAR(50);

UPDATE defect
SET defect_id = 'DEF-001',
    description = 'Surface paint scratch',
    penalty_points = 10
WHERE code = 'PAINT_SCRATCH';

UPDATE defect
SET defect_id = 'DEF-002',
    description = 'Battery cell failure',
    penalty_points = 35
WHERE code = 'BATTERY_FAILURE';

UPDATE defect
SET defect_id = 'DEF-003',
    description = 'Door does not close flush',
    penalty_points = 15
WHERE code = 'DOOR_ALIGNMENT';

UPDATE defect
SET defect_id = 'DEF-004',
    description = 'Seat not installed',
    penalty_points = 20
WHERE code = 'MISSING_SEAT';

ALTER TABLE defect
    ADD CONSTRAINT uq_defect_defect_id UNIQUE (defect_id);
