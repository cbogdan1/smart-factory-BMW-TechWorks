package com.smartfactory.planning.boundary.kafka;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.PlanningServiceExceptions;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import com.smartfactory.planning.control.service.ProductionPlanService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
public class VehicleStageAdvancedConsumer {
    @Inject
    ProductionPlanService productionPlanService;

    @Incoming(Topics.VEHICLE_STAGE_ADVANCED)
    @Blocking
    public void consume(final VehicleStageAdvancedEvent vehicleStageAdvancedEvent) {
        if (vehicleStageAdvancedEvent == null || vehicleStageAdvancedEvent.event() == null) {
            throw new BusinessException("Vehicle-stage-advanced event must not be null",
                    PlanningServiceExceptions.INVALID_VEHICLE_STAGE_ADVANCED_EVENT, 400);
        }

        final var event = vehicleStageAdvancedEvent.event();

        if (event.payload() == null) {
            throw new BusinessException("Vehicle-stage-advanced payload must not be null",
                    PlanningServiceExceptions.INVALID_VEHICLE_STAGE_ADVANCED_EVENT, 400);
        }

        final VehicleStageAdvancedPayload payload = event.payload();

        if (!Topics.VEHICLE_STAGE_ADVANCED.equals(event.eventType())) {
            throw new BusinessException(
                    "Expected eventType " + Topics.VEHICLE_STAGE_ADVANCED + " but received " + event.eventType(),
                    PlanningServiceExceptions.INVALID_EVENT_TYPE, 400);
        }

        try {
            productionPlanService.advanceVehicleStage(event.eventId(), payload);
        } catch (final BusinessException e) {
            log.error(e.getMessage());
        }
    }
}