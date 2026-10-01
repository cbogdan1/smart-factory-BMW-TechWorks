package com.smartfactory.common.event;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Mandatory event envelope for <b>every</b> Kafka message on the platform.
 * Every published event wraps its topic-specific business data
 * in {@code payload} and carries the shared metadata below.
 *
 * <p>The Kafka message key must be {@link #correlationId()} (the {@code vehicleId}), so all
 * events for a vehicle land on the same partition and preserve ordering.
 *
 * @param eventId       unique event id, format {@code EVT-...}
 * @param eventType     business event name = the Kafka topic name (see {@link com.smartfactory.common.Topics})
 * @param eventVersion  schema version, starts at {@code "1.0"}
 * @param timestamp     when the event occurred (UTC, ISO-8601 with trailing {@code Z})
 * @param sourceSystem  the publishing service, e.g. {@code order-service}
 * @param correlationId tracks one vehicle/order across services (defaults to {@code vehicleId})
 * @param payload       topic-specific business data
 * @param <T>           the concrete payload type (a service-owned record)
 */
@JsonPropertyOrder({ "eventId", "eventType", "eventVersion", "timestamp", "sourceSystem", "correlationId", "payload" })
public record DomainEvent<T>(@NotBlank String eventId, @NotBlank String eventType, @NotBlank String eventVersion,
                             @NotNull Instant timestamp, @NotNull String sourceSystem, @NotNull String correlationId,
                             @Valid @NotNull T payload) {
    /**
     * Default schema version for newly created events.
     */
    public static final String CURRENT_VERSION = "1.0";

    /**
     * Convenience constructor that stamps {@code eventVersion = "1.0"} and
     * {@code timestamp = now()} for you, so producers only supply the meaningful fields.
     * Provide your own {@code eventId}.
     */
    public DomainEvent(final String eventId, final String eventType, final String sourceSystem,
            final String correlationId, final T payload) {
        this(eventId, eventType, CURRENT_VERSION, Instant.now(), sourceSystem, correlationId, payload);
    }

    /**
     * The Kafka partition key for this event (the {@code correlationId} / vehicleId).
     */
    public String kafkaKey() {
        return correlationId;
    }
}

