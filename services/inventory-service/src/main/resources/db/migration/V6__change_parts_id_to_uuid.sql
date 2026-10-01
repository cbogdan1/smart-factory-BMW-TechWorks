CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- Remove FK if it exists
ALTER TABLE material_demands
DROP CONSTRAINT IF EXISTS fk_material_demands_required_part;




-- Remove the bad BIGINT default
ALTER TABLE material_demands
    ALTER COLUMN required_part_id DROP DEFAULT;




-- Convert parts.id to UUID
ALTER TABLE parts
DROP CONSTRAINT IF EXISTS parts_pkey;


ALTER TABLE parts
    ALTER COLUMN id DROP DEFAULT;


ALTER TABLE parts
ALTER COLUMN id TYPE UUID
USING gen_random_uuid();


ALTER TABLE parts
    ADD PRIMARY KEY (id);




-- Convert material_demands.required_part_id to UUID
ALTER TABLE material_demands
ALTER COLUMN required_part_id TYPE UUID
USING gen_random_uuid();




-- Recreate FK
ALTER TABLE material_demands
    ADD CONSTRAINT fk_material_demands_required_part
        FOREIGN KEY (required_part_id)
            REFERENCES parts(id);



