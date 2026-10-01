CREATE TABLE vehicle_bom (
                             bom_id        VARCHAR PRIMARY KEY,
                             vehicle_model VARCHAR NOT NULL,   -- e.g. 'BMW_I4', 'BMW_X5'
                             part_code     VARCHAR NOT NULL,   -- FK-by-code to part.part_code
                             quantity      INT     NOT NULL
);