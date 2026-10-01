CREATE TABLE production_plan
(
    plan_id      BIGSERIAL PRIMARY KEY,
    order_id     BIGINT      NOT NULL,
    vehicle_id   BIGINT      NOT NULL,
    created_date TIMESTAMP   NOT NULL,
    status       VARCHAR(50) NOT NULL
);