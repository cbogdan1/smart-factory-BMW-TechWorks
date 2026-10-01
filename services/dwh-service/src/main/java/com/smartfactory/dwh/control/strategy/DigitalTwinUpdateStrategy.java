package com.smartfactory.dwh.control.strategy;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.dwh.entity.VehicleTwinEntity;

public interface DigitalTwinUpdateStrategy {
    boolean supports(String eventType);

    void update(VehicleTwinEntity twin, DomainEvent<?> event);
}
