ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS customer_email VARCHAR(255),
    ADD COLUMN IF NOT EXISTS priority VARCHAR(50),
    ADD COLUMN IF NOT EXISTS last_modified_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS completion_date TIMESTAMP;


/*set lastModifiedDate with createdDate*/
UPDATE orders
SET last_modified_date = created_date
WHERE last_modified_date IS NULL;