CREATE TABLE production_line
(
    line_id          VARCHAR(50) PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    maximum_capacity INTEGER      NOT NULL CHECK (maximum_capacity >= 0),
    current_load     INTEGER      NOT NULL CHECK (current_load >= 0),
    enabled          BOOLEAN      NOT NULL
)