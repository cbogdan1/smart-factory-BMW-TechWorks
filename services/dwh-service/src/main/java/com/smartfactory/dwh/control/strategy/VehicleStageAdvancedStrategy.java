package com.smartfactory.dwh.control.strategy;

import java.time.Instant;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.DataWarehouseErrorCodes;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleStageAdvancedStrategy implements DigitalTwinUpdateStrategy {

    @Override
    public boolean supports(final String eventType) {
        return eventType.equalsIgnoreCase(Topics.VEHICLE_STAGE_ADVANCED) || eventType.equalsIgnoreCase(
                "vehicle-stage-advanced");
    }

    @Override
    public void update(final VehicleTwinEntity twin, final DomainEvent<?> event) {
        if (!(event.payload() instanceof final VehicleStageAdvancedPayload payload)) {
            throw new BusinessException("Invalid payload for VehicleStageAdvanced event",
                    DataWarehouseErrorCodes.INVALID_EVENT, 400);
        }

        twin.setCurrentStage(payload.currentStage().toString());
        twin.setLastUpdated(Instant.now());
    }
}