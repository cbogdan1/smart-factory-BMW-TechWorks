package com.smartfactory.assembly.boundary.producer;

import static com.smartfactory.common.Topics.MATERIAL_REQUESTED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE;

import java.util.UUID;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.MaterialRequestedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Slf4j
@ApplicationScoped
public class MaterialRequestProducer {
    @Inject
    @Channel(MATERIAL_REQUESTED)
    Emitter<MaterialRequestedEvent> emitter;

    public void publishMaterialRequest(final MaterialRequestedPayload materialRequestedPayload) {
        if (materialRequestedPayload == null) {
            throw new BusinessException(NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE, NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_CODE, 400);
        }

        final String orderId = materialRequestedPayload.orderId();
        final String eventId = "EVT-" + UUID.randomUUID();
        final String vehicleId = materialRequestedPayload.vehicleId();
        final String material = materialRequestedPayload.material();

        final DomainEvent<MaterialRequestedPayload> materialRequestedEvent =
                new DomainEvent<>(eventId, MATERIAL_REQUESTED, "assembly-service", vehicleId, materialRequestedPayload);

        final MaterialRequestedEvent event = new MaterialRequestedEvent(materialRequestedEvent);

        emitter.send(event).whenComplete((unused, throwable) -> {
            if (throwable != null) {
                log.error("Failed to publish material request event", throwable);
            } else {
                log.info("Published material request: orderId={}, eventId={}, vehicleId={}, material={}", orderId,
                        eventId, vehicleId, material);
            }
        });
    }
}
