package com.smartfactory.dwh.control.strategy;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PartsReservedStrategy implements DigitalTwinUpdateStrategy {

    @Override
    public boolean supports(final String eventType) {
        return eventType.equalsIgnoreCase("parts-reserved");
    }

    @Override
    public void update(final VehicleTwinEntity twin, final DomainEvent<?> event) {
        twin.setStatus("MATERIALS_SECURED");
        twin.setLastUpdated(Instant.now());
    }
}
