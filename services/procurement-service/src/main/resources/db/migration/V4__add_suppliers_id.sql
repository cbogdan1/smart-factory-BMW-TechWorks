ALTER TABLE supplier_part
DROP
CONSTRAINT fk_supplier_part_supplier;

ALTER TABLE suppliers
DROP
CONSTRAINT suppliers_pkey;

ALTER TABLE suppliers
    ADD COLUMN id BIGSERIAL PRIMARY KEY;

ALTER TABLE suppliers
    ADD CONSTRAINT suppliers_supplier_id_key UNIQUE (supplier_id);

ALTER TABLE suppliers
    ADD CONSTRAINT suppliers_name_key UNIQUE (name);

ALTER TABLE supplier_part
    ADD CONSTRAINT fk_supplier_part_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers (supplier_id);