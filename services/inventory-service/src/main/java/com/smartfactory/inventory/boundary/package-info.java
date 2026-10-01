/**
 * <b>Boundary</b> layer (ECB) — the edge of the service / adapters to the outside world.
 *
 * <p>Everything that talks to actors or other systems lives here:
 * <ul>
 *   <li>JAX-RS REST resources (e.g. {@code PingResource})</li>
 *   <li>Kafka adapters — {@code @Incoming} consumers and {@code @Outgoing}/{@code Emitter} producers</li>
 *   <li>Request/response DTOs and mappers</li>
 * </ul>
 *
 * <p>Boundary classes stay thin: validate/translate the external call, delegate to
 * {@code control}, and translate the result back. They may depend on {@code control}
 * and {@code entity}, but <b>nothing may depend on the boundary</b>.
 */
package com.smartfactory.inventory.boundary;

