CREATE TABLE purchase_order
(
    id                BIGSERIAL PRIMARY KEY,
    purchase_order_id VARCHAR(255) NOT NULL UNIQUE,
    part_id           VARCHAR(255) NOT NULL,
    supplier_id       VARCHAR(255) NOT NULL,
    quantity          INT          NOT NULL,
    status            VARCHAR(50)  NOT NULL,
    order_date        TIMESTAMP    NOT NULL,
    delivery_date     TIMESTAMP,
    total_price       NUMERIC(19, 2)
);