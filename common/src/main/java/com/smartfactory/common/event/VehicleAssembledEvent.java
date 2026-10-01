package com.smartfactory.common.event;

import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;

public record VehicleAssembledEvent(DomainEvent<VehicleAssembledPayload> event) {
}
