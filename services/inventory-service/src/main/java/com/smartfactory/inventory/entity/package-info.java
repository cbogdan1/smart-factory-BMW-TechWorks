/**
 * <b>Entity</b> layer (ECB) — domain model and persistence.
 *
 * <p>Hibernate/Panache entities and repositories that own this service's private schema.
 *
 * <p><b>Flyway rule:</b> every change to a mapped entity structure MUST be paired with a
 * Flyway migration under {@code src/main/resources/db/migration} (V2__…, V3__…). The app
 * runs with {@code quarkus.hibernate-orm.database.generation=validate}, so an entity that
 * doesn't match the migrated schema will fail startup — by design.
 *
 * <p>Entity is the innermost layer: it depends on nothing else in this service.
 */
package com.smartfactory.inventory.entity;

