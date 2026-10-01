package com.smartfactory.common.event;

import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;

public record VehicleStageAdvancedEvent(DomainEvent<VehicleStageAdvancedPayload> event) {
}
