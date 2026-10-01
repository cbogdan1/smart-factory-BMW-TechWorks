/**
 * <b>Control</b> layer (ECB) — application / business logic and orchestration.
 *
 * <p>CDI beans (typically {@code @ApplicationScoped}) that implement the use cases:
 * validate input, coordinate entities and repositories, publish domain events, and
 * own transactions ({@code @Transactional}).
 *
 * <p>Dependency rule: Control depends on {@code entity}; it must <b>not</b> depend on
 * {@code boundary}. Keep transport concerns (HTTP, Kafka) out of this layer.
 */
package com.smartfactory.inventory.control;

