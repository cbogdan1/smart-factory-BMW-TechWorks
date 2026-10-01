package com.smartfactory.assembly.boundary.producer;

import static com.smartfactory.common.Topics.VEHICLE_STAGE_ADVANCED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE;

import java.util.UUID;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Slf4j
@ApplicationScoped
public class VehicleStageAdvanceProducer {
    @Inject
    @Channel(VEHICLE_STAGE_ADVANCED)
    Emitter<VehicleStageAdvancedEvent> emitter;

    public void publishAdvanceRequest(final VehicleStageAdvancedPayload vehicleStageAdvancedPayload) {
        if (vehicleStageAdvancedPayload == null) {
            throw new BusinessException(NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE, NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_CODE, 400);
        }

        final String eventId = "EVT-" + UUID.randomUUID();
        final String vehicleId = vehicleStageAdvancedPayload.vehicleId();

        final DomainEvent<VehicleStageAdvancedPayload> vehicleStageAdvanceEvent =
                new DomainEvent<>(eventId, VEHICLE_STAGE_ADVANCED, "assembly-service", vehicleId,
                        vehicleStageAdvancedPayload);

        final VehicleStageAdvancedEvent event = new VehicleStageAdvancedEvent(vehicleStageAdvanceEvent);

        emitter.send(event).whenComplete((unused, throwable) -> {
            if (throwable != null) {
                log.error("Failed to publish vehicle advance event", throwable);
            } else {
                log.info("Published vehicle advance request: eventId={}, vehicleId={}", eventId, vehicleId);
            }
        });
    }
}
