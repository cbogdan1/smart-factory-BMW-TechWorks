package com.smartfactory.inventory.boundary;

import static com.smartfactory.common.Topics.PARTS_SHORTAGE_DETECTED;

import java.util.UUID;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsShortageDetectedEvent;
import com.smartfactory.common.payloads.procurement.PartsShortageDetectedPayload;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@Slf4j
public class ShortageEventPublisher {

    @Inject
    @Channel(PARTS_SHORTAGE_DETECTED)
    Emitter<Record<String, PartsShortageDetectedEvent>> shortageEmitter;

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void publishShortage(
            final String vehicleId,
            final String planId,
            final String partId,
            final String partName,
            final int requiredQuantity,
            final int availableQuantity,
            final int missingQuantity) {
        log.info("Publishing shortage event for vehicleId={} and partId={}. Missing quantity: {}",
                vehicleId, partId, missingQuantity);

        final PartsShortageDetectedPayload payload =
                new PartsShortageDetectedPayload(
                        vehicleId,
                        planId,
                        partId,
                        partName,
                        requiredQuantity,
                        availableQuantity,
                        missingQuantity
                );

        final DomainEvent<PartsShortageDetectedPayload> event =
                new DomainEvent<>(
                        "EVT-" + UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase(), PARTS_SHORTAGE_DETECTED,
                        "inventory-service",
                        vehicleId,
                        payload
                );

        final PartsShortageDetectedEvent shortageEvent =
                new PartsShortageDetectedEvent(event);

        shortageEmitter.send(
                Record.of(vehicleId, shortageEvent)
        );
    }
}