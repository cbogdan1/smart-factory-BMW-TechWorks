CREATE TABLE orders
(
    order_id      VARCHAR(100) PRIMARY KEY,
    vehicle_id    VARCHAR(100) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    vehicle_model VARCHAR(255) NOT NULL,
    color         VARCHAR(100) NOT NULL,
    battery_type  VARCHAR(50)  NOT NULL,
    status        VARCHAR(50)  NOT NULL,
    created_date  TIMESTAMP    NOT NULL
);