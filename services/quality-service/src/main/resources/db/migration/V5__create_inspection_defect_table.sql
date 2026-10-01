CREATE TABLE inspection_defect
(
    inspection_defect_id VARCHAR(50) PRIMARY KEY,
    inspection_id        VARCHAR(50) NOT NULL,
    defect_code          VARCHAR(50) NOT NULL,
    comment              VARCHAR(500),
    created_date         TIMESTAMP   NOT NULL,
    CONSTRAINT fk_inspection_defect_inspection
        FOREIGN KEY (inspection_id) REFERENCES inspection (inspection_id),
    CONSTRAINT fk_inspection_defect_defect
        FOREIGN KEY (defect_code) REFERENCES defect (code),
    CONSTRAINT uq_inspection_defect UNIQUE (inspection_id, defect_code)
);

CREATE INDEX idx_inspection_defect_inspection_id
    ON inspection_defect (inspection_id);