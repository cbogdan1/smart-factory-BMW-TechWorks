package com.smartfactory.dwh.control.strategy;

import java.time.Instant;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QualityReworkRequiredStrategy implements DigitalTwinUpdateStrategy {

    @Override
    public boolean supports(final String eventType) {
        return eventType.equalsIgnoreCase(Topics.QUALITY_REWORK_REQUIRED) || eventType.equalsIgnoreCase(
                "quality-rework-required");
    }

    @Override
    public void update(final VehicleTwinEntity twin, final DomainEvent<?> event) {
        twin.setQualityStatus("REWORK");
        final int currentReworks = twin.getReworkCount() != null ? twin.getReworkCount() : 0;
        twin.setReworkCount(currentReworks + 1);
        twin.setLastUpdated(Instant.now());
    }
}