INSERT INTO parts (id, part_id, part_code, part_name, description, unit_of_measure, active)
VALUES ('11111111-1111-1111-1111-111111111111', 'PART-001', 'BAT-LONGRANGE', 'Long Range Battery',
        'High capacity EV battery', 'PCS', true),
       ('22222222-2222-2222-2222-222222222222', 'PART-002', 'SEAT-STANDARD', 'Standard Seat', 'Standard fabric seat',
        'PCS', true),
       ('33333333-3333-3333-3333-333333333333', 'PART-003', 'PAINT-METALLIC', 'Metallic Paint Kit',
        'Metallic paint bucket', 'PCS', true),
       ('44444444-4444-4444-4444-444444444444', 'PART-004', 'WHEEL-19INCH', '19" Alloy Wheel', 'Aluminum alloy wheel',
        'PCS', true),
       ('55555555-5555-5555-5555-555555555555', 'PART-005', 'ENG-V6', 'V6 Engine', '3.0L V6 Engine', 'PCS', true),
       ('66666666-6666-6666-6666-666666666666', 'PART-006', 'SEAT-PREMIUM', 'Premium Seat', 'Premium leather seat',
        'PCS', true),
       ('77777777-7777-7777-7777-777777777777', 'PART-007', 'PAINT-SOLID', 'Solid Paint Kit', 'Solid paint kit', 'PCS',
        true),
       ('88888888-8888-8888-8888-888888888888', 'PART-008', 'WHEEL-21INCH', '21" Alloy Wheel',
        '21" Premium alloy wheel', 'PCS', true) ON CONFLICT (part_id) DO NOTHING;

INSERT INTO inventory_stock (part_id, available_quantity, reserved_quantity, minimum_quantity, maximum_quantity)
VALUES ('11111111-1111-1111-1111-111111111111', 50, 0, 5, 100),
       ('22222222-2222-2222-2222-222222222222', 80, 0, 10, 200),
       ('33333333-3333-3333-3333-333333333333', 30, 0, 5, 100),
       ('44444444-4444-4444-4444-444444444444', 60, 0, 8, 200),
       ('55555555-5555-5555-5555-555555555555', 20, 0, 3, 50),
       ('66666666-6666-6666-6666-666666666666', 40, 0, 5, 100),
       ('77777777-7777-7777-7777-777777777777', 25, 0, 5, 100),
       ('88888888-8888-8888-8888-888888888888', 30, 0, 5, 100) ON CONFLICT (part_id) DO NOTHING;