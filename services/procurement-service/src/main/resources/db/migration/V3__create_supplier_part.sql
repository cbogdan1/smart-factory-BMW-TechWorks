CREATE TABLE supplier_part
(
    supplier_part_id BIGSERIAL PRIMARY KEY,
    supplier_id      VARCHAR(50)    NOT NULL,
    part_code        VARCHAR(100)   NOT NULL,
    unit_cost        DECIMAL(12, 2) NOT NULL,
    lead_time_days   INTEGER        NOT NULL,
    created_date     DATE           NOT NULL,

    CONSTRAINT fk_supplier_part_supplier
        FOREIGN KEY (supplier_id)
            REFERENCES suppliers (supplier_id),

    CONSTRAINT uq_supplier_part
        UNIQUE (supplier_id, part_code)
);