CREATE TABLE node
(
    id           BIGSERIAL PRIMARY KEY,
    node_id      VARCHAR(255) UNIQUE NOT NULL,
    name         VARCHAR(255) UNIQUE NOT NULL,
    type         VARCHAR(50)         NOT NULL,
    created_date TIMESTAMP           NOT NULL
);