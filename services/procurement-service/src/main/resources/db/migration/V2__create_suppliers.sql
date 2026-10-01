CREATE TABLE suppliers
(
    supplier_id    VARCHAR(50) PRIMARY KEY,
    name           VARCHAR(255)  NOT NULL,
    lead_time_days INTEGER       NOT NULL,
    rating         DECIMAL(3, 2) NOT NULL,
    contact_email  VARCHAR(255)  NOT NULL UNIQUE,
    active         BOOLEAN       NOT NULL,
    created_date   DATE          NOT NULL
);