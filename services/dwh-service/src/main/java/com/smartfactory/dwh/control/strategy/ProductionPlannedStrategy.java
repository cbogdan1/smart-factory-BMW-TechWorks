package com.smartfactory.dwh.control.strategy;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductionPlannedStrategy implements DigitalTwinUpdateStrategy {

    @Override
    public boolean supports(final String eventType) {
        return eventType.equalsIgnoreCase("production-planned");
    }

    @Override
    public void update(final VehicleTwinEntity twin, final DomainEvent<?> event) {
        if (event.payload() instanceof final ProductionPlannedPayload payload) {
            twin.setStatus("PLANNED");

            if (payload.productionLine() != null) {
                twin.setCurrentLocation(payload.productionLine());
            }

            if (payload.vehicleModel() != null) {
                twin.setVehicleModel(payload.vehicleModel());
            }

            twin.setLastUpdated(Instant.now());
        }
    }
}
