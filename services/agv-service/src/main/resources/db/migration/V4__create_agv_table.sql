CREATE TABLE agv
(
    id              BIGSERIAL PRIMARY KEY,
    agv_id          VARCHAR(50)  NOT NULL UNIQUE,
    status          VARCHAR(20)  NOT NULL,
    current_node_id VARCHAR(255) NOT NULL,
    battery_level   INTEGER      NOT NULL,
    created_date    TIMESTAMP    NOT NULL,
    last_updated    TIMESTAMP    NOT NULL,

    CONSTRAINT fk_agv_node
        FOREIGN KEY (current_node_id)
            REFERENCES node (node_id)
);