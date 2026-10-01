CREATE TABLE defect
(
    code           VARCHAR(50) PRIMARY KEY,
    description    VARCHAR(255) NOT NULL,
    penalty_points INTEGER      NOT NULL,
    active         BOOLEAN      NOT NULL,
    created_date   TIMESTAMP    NOT NULL,
    affected_stage VARCHAR(30)
);

CREATE INDEX idx_defect_active
    ON defect (active);