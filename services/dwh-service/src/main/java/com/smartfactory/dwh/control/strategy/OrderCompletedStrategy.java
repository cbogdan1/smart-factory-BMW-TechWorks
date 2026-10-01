package com.smartfactory.dwh.control.strategy;

import java.time.Instant;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderCompletedStrategy implements DigitalTwinUpdateStrategy {

    @Override
    public boolean supports(final String eventType) {
        return eventType.equalsIgnoreCase(Topics.ORDER_COMPLETED) || eventType.equalsIgnoreCase("order-completed");
    }

    @Override
    public void update(final VehicleTwinEntity twin, final DomainEvent<?> event) {
        twin.setStatus("COMPLETED");
        twin.setLastUpdated(Instant.now());
    }
}