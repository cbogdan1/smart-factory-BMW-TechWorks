package com.smartfactory.common.event;

import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;

public record MaterialDeliveredEvent(DomainEvent<MaterialDeliveredPayload> event) {
}
