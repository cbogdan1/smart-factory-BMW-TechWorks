package com.smartfactory.common.event;

import com.smartfactory.common.payloads.order_service.OrderCompletedPayload;

public record OrderCompletedEvent(DomainEvent<OrderCompletedPayload> event) {
}
