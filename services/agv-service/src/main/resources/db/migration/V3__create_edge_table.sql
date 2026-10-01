CREATE TABLE edge
(
    id              BIGSERIAL PRIMARY KEY,
    edge_id         VARCHAR(255) UNIQUE NOT NULL,
    source_node_id  VARCHAR(255)        NOT NULL,
    target_node_id  VARCHAR(255)        NOT NULL,
    distance_meters INT                 NOT NULL,
    created_date    TIMESTAMP           NOT NULL,
    FOREIGN KEY (source_node_id) REFERENCES node (node_id),
    FOREIGN KEY (target_node_id) REFERENCES node (node_id)
);