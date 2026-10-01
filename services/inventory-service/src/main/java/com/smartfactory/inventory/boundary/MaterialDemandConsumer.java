package com.smartfactory.inventory.boundary;

import static com.smartfactory.common.Topics.PARTS_RESERVED;
import static com.smartfactory.common.Topics.PRODUCTION_PLANNED;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.common.dto.inventory.ReservedPart;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsReservedEvent;
import com.smartfactory.common.event.ProductionPlannedEvent;
import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.inventory.control.MaterialDemandService;
import com.smartfactory.inventory.control.ReservationService;
import com.smartfactory.inventory.control.exception.ProductionPlannedEventEmptyException;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
public class MaterialDemandConsumer {

    @Inject
    MaterialDemandService materialDemandService;

    @Inject
    ReservationService reservationService;

    @Inject
    @Channel(PARTS_RESERVED)
    Emitter<Record<String, PartsReservedEvent>> partsReservedEmitter;

    /**
     * Consume incoming Production Planned Event, and create material demands based on its
     * contents
     * @param productionPlannedEvent the incoming Kafka event
     */
    @Incoming(PRODUCTION_PLANNED)
    public void consumeProductionPlanned(final ProductionPlannedEvent productionPlannedEvent) {

        // Check if event is null
        if (productionPlannedEvent == null
                || productionPlannedEvent.event() == null
                || productionPlannedEvent.event().eventId() == null
                || productionPlannedEvent.event().payload() == null) {
            log.warn("Received null production planned event");
            throw new ProductionPlannedEventEmptyException();
        }

        // Extract the domain event
        final DomainEvent<ProductionPlannedPayload> event = productionPlannedEvent.event();

        // Get the event Id
        final String eventId = event.eventId();

        // Get the event payload
        final ProductionPlannedPayload payload = event.payload();

        // Call to materialDemandService to actually create records of the material demands
        final boolean demandsCreated = materialDemandService.recordDemand(payload, eventId);

        // Check if material demands were actually recorded before reserving the parts
        if(!demandsCreated){
            log.warn("Skipping reservation for event {} because no material demands were created.", eventId);
            return;
        }

        log.info("Material demand for event ID {} has been persisted.", eventId);

        log.info("Processing production-planned event with ID: {}", eventId);

        final List<ReservationResponse> reservationResponses =
                reservationService.reserveMaterialForDemand(eventId);

        for (final ReservationResponse reservationResponse : reservationResponses) {
            final PartsReservedEvent reservedEvent =
                    createPartsReservedEvent(reservationResponse, event);

            partsReservedEmitter.send(
                    Record.of(reservationResponse.getVehicleId(), reservedEvent)
            );

            log.info(
                    "Published PartsReservedEvent for reservation ID: {}",
                    reservationResponse.getReservationId()
            );
        }
    }

    private PartsReservedEvent createPartsReservedEvent(final ReservationResponse reservation,
            final DomainEvent<ProductionPlannedPayload> domainEvent) {
        final ReservedPart reservedPart =
                new ReservedPart(reservation.getPartCode(), reservation.getQuantity().intValue());

        final PartsReservedPayload payload =
                new PartsReservedPayload(reservation.getReservationId(), reservation.getVehicleId(),
                        domainEvent.payload().planId(), List.of(reservedPart), Instant.now().toString());

        final DomainEvent<PartsReservedPayload> event =
                new DomainEvent<>("EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), PARTS_RESERVED,
                        "inventory-service", reservation.getVehicleId(), payload);

        return new PartsReservedEvent(event);
    }
}
