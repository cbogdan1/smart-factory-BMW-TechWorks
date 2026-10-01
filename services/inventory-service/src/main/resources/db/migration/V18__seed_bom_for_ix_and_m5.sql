-- Seeds the parts, stock and BOM recipes for BMW_IX and BMW_M5 so that every vehicle model the
-- Order service accepts can actually be produced end to end.
--
-- Background: Order validation accepts BMW_I4, BMW_IX and BMW_M5 (see the *Validator strategies in
-- order-service), but V16 only seeded BOMs for BMW_I4 and BMW_X5. A BMW_IX or BMW_M5 order was
-- therefore accepted, planned, and then silently stalled in Inventory:
-- MaterialDemandService logs "No BOM found for vehicle model ..." and returns false, so no parts are
-- reserved, no parts-reserved event is published, and Assembly never starts production.
--
-- After this migration the demo can use any of BMW_I4 / BMW_IX / BMW_M5.
-- (BMW_X5 keeps its BOM from V16 but remains non-orderable: there is no validator for it.)

-- ---------------------------------------------------------------------------
-- 1. New parts required by the two recipes
-- ---------------------------------------------------------------------------
INSERT INTO parts (id, part_id, part_code, part_name, description, unit_of_measure, active)
VALUES ('99999999-9999-9999-9999-999999999999', 'PART-009', 'BAT-PERFORMANCE', 'Performance Battery',
        'High output EV battery for performance models', 'PCS', true),
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'PART-010', 'BAT-STANDARD', 'Standard Battery',
        'Standard range hybrid battery', 'PCS', true),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'PART-011', 'ENG-V8', 'V8 Engine', '4.4L twin-turbo V8 engine', 'PCS',
        true),
       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'PART-012', 'SEAT-SPORT', 'Sport Seat',
        'Bucket sport seat with side bolsters', 'PCS', true),
       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'PART-013', 'WHEEL-20INCH', '20" Alloy Wheel',
        '20" forged alloy wheel', 'PCS', true) ON CONFLICT (part_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 2. Stock for the new parts (generous, so the demo can be repeated many times)
-- ---------------------------------------------------------------------------
-- Roughly 30 builds' worth: reserved stock is never returned to available_quantity, so every
-- demo run permanently consumes parts.
INSERT INTO inventory_stock (part_id, available_quantity, reserved_quantity, minimum_quantity, maximum_quantity)
VALUES ('99999999-9999-9999-9999-999999999999', 120, 0, 10, 200), -- BAT-PERFORMANCE, 1 per iX
       ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 120, 0, 10, 200), -- BAT-STANDARD,    1 per M5
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 60, 0, 5, 100),   -- ENG-V8,          1 per M5
       ('cccccccc-cccc-cccc-cccc-cccccccccccc', 160, 0, 15, 300), -- SEAT-SPORT,      4 per M5
       ('dddddddd-dddd-dddd-dddd-dddddddddddd', 160, 0, 15, 300)  -- WHEEL-20INCH,    4 per M5
    ON CONFLICT (part_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 3. BMW iX recipe - electric SUV (battery: LONG_RANGE or PERFORMANCE)
-- ---------------------------------------------------------------------------
INSERT INTO vehicle_bom (bom_id, vehicle_model, part_code, quantity)
VALUES ('BOM-009', 'BMW_IX', 'BAT-PERFORMANCE', 1),
       ('BOM-010', 'BMW_IX', 'SEAT-PREMIUM', 5),
       ('BOM-011', 'BMW_IX', 'PAINT-METALLIC', 1),
       ('BOM-012', 'BMW_IX', 'WHEEL-21INCH', 4) ON CONFLICT (bom_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 4. BMW M5 recipe - performance sedan (battery: STANDARD_RANGE)
-- ---------------------------------------------------------------------------
INSERT INTO vehicle_bom (bom_id, vehicle_model, part_code, quantity)
VALUES ('BOM-013', 'BMW_M5', 'ENG-V8', 1),
       ('BOM-014', 'BMW_M5', 'BAT-STANDARD', 1),
       ('BOM-015', 'BMW_M5', 'SEAT-SPORT', 4),
       ('BOM-016', 'BMW_M5', 'PAINT-SOLID', 1),
       ('BOM-017', 'BMW_M5', 'WHEEL-20INCH', 4) ON CONFLICT (bom_id) DO NOTHING;

-- ---------------------------------------------------------------------------
-- 5. Top up the parts that are now shared between several orderable models
-- ---------------------------------------------------------------------------
-- Reserved stock is never returned to `available_quantity`, so every demo run permanently consumes
-- parts. With the V13 quantities the binding constraint was WHEEL-21INCH (30 pcs / 4 per car = only
-- 7 BMW_IX builds), which is too tight for a classroom where many students each run three flows.
-- These quantities give roughly 30 builds per model. `maximum_quantity` is raised alongside to keep
-- the chk_inventory_stock_minimum_maximum constraint coherent.
UPDATE inventory_stock s
SET available_quantity = v.available,
    maximum_quantity   = v.maximum
FROM (VALUES ('BAT-LONGRANGE', 120::BIGINT, 200::BIGINT), -- BMW_I4
             ('SEAT-STANDARD', 160, 300),                 -- BMW_I4
             ('WHEEL-19INCH', 160, 300),                  -- BMW_I4
             ('PAINT-METALLIC', 120, 200),                -- BMW_I4 + BMW_IX
             ('SEAT-PREMIUM', 200, 300),                  -- BMW_IX (+ BMW_X5)
             ('WHEEL-21INCH', 160, 300),                  -- BMW_IX (+ BMW_X5)
             ('PAINT-SOLID', 120, 200)                    -- BMW_M5 (+ BMW_X5)
     ) AS v(part_code, available, maximum)
         JOIN parts p ON p.part_code = v.part_code
WHERE s.part_id = p.id
  AND s.available_quantity < v.available;

