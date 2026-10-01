CREATE TABLE purchase_order_status_history
(
    id                 BIGSERIAL PRIMARY KEY,
    purchase_order_id  VARCHAR(255)             NOT NULL,
    old_status         VARCHAR(50)              NOT NULL,
    new_status         VARCHAR(50)              NOT NULL,
    last_modified_date TIMESTAMP WITH TIME ZONE NOT NULL
);
