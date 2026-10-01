ALTER TABLE reservations
DROP CONSTRAINT IF EXISTS chk_reservations_quantity;

ALTER TABLE reservations
    ADD CONSTRAINT chk_reservations_quantity
        CHECK (quantity_reserved >= 0);


