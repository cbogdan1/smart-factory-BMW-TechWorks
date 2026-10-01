package com.smartfactory.dwh.control.strategy;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderCreatedStrategy implements DigitalTwinUpdateStrategy {

    @Override
    public boolean supports(final String eventType) {
        return eventType.equalsIgnoreCase("order-created");
    }

    @Override
    public void update(final VehicleTwinEntity twin, final DomainEvent<?> event) {
        twin.setStatus("CREATED");
        if (event.payload() instanceof final OrderCreatedPayload payload) {
            twin.setOrderId(payload.orderId());
            twin.setVehicleModel(payload.vehicleModel());
            twin.setLastUpdated(Instant.now());
        }
    }
}
