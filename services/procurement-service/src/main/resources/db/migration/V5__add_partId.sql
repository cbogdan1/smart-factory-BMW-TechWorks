ALTER TABLE supplier_part
DROP CONSTRAINT uq_supplier_part;

ALTER TABLE supplier_part
DROP COLUMN part_code;

ALTER TABLE supplier_part
    ADD COLUMN part_id VARCHAR(255) NOT NULL;

ALTER TABLE supplier_part
    ADD CONSTRAINT uq_supplier_part
        UNIQUE (supplier_id, part_id);