package com.smartfactory.common.event;

import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;

public record MaterialRequestedEvent(DomainEvent<MaterialRequestedPayload> event) {
}
