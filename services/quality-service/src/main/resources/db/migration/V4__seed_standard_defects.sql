INSERT INTO defect (code, description, penalty_points, active, created_date, affected_stage)
VALUES ('PAINT_SCRATCH', 'Paint scratch detected', 5, TRUE, CURRENT_TIMESTAMP, 'PAINT'),
       ('BATTERY_FAILURE', 'Battery failure detected', 15, TRUE, CURRENT_TIMESTAMP, 'POWERTRAIN'),
       ('DOOR_ALIGNMENT', 'Door alignment issue', 8, TRUE, CURRENT_TIMESTAMP, 'INTERIOR'),
       ('MISSING_SEAT', 'Seat is missing', 20, TRUE, CURRENT_TIMESTAMP, 'INTERIOR');