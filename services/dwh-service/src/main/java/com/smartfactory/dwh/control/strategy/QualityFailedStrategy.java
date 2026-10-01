package com.smartfactory.dwh.control.strategy;

import java.time.Instant;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QualityFailedStrategy implements DigitalTwinUpdateStrategy {

    @Override
    public boolean supports(final String eventType) {
        return eventType.equalsIgnoreCase(Topics.QUALITY_FAILED) || eventType.equalsIgnoreCase("quality-failed");
    }

    @Override
    public void update(final VehicleTwinEntity twin, final DomainEvent<?> event) {
        twin.setQualityStatus("FAILED");
        twin.setLastUpdated(Instant.now());
    }
}