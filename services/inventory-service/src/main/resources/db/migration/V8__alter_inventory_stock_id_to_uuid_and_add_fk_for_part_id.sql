DROP TABLE inventory_stock;


CREATE TABLE inventory_stock
(
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    part_id UUID NOT NULL,
    available_quantity BIGINT NOT NULL,
    reserved_quantity BIGINT NOT NULL,
    minimum_quantity BIGINT NOT NULL,
    maximum_quantity BIGINT NOT NULL,

    CONSTRAINT pk_inventory_stock PRIMARY KEY (id),

    CONSTRAINT uk_inventory_stock_part_id UNIQUE (part_id),

    CONSTRAINT fk_inventory_stock_part
        FOREIGN KEY (part_id)
            REFERENCES parts(id),

    CONSTRAINT chk_inventory_stock_available
        CHECK (available_quantity >= 0),

    CONSTRAINT chk_inventory_stock_minimum_maximum
        CHECK (minimum_quantity <= maximum_quantity),

    CONSTRAINT chk_inventory_stock_reserved
        CHECK (reserved_quantity >= 0),

    CONSTRAINT chk_inventory_stock_minimum
        CHECK (minimum_quantity >= 0),

    CONSTRAINT chk_inventory_stock_maximum
        CHECK (maximum_quantity >= 0)
);


