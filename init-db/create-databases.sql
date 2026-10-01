-- One database per service (logical ownership: no cross-service DB access).
CREATE DATABASE orderdb;
CREATE DATABASE planningdb;
CREATE DATABASE inventorydb;
CREATE DATABASE procurementdb;
CREATE DATABASE assemblydb;
CREATE DATABASE agvdb;
CREATE DATABASE qualitydb;
CREATE DATABASE dwhdb;
-- All owned by the shared 'factory' user for simplicity in the course.
-- (For stricter isolation, create one role per service and GRANT per database.)

