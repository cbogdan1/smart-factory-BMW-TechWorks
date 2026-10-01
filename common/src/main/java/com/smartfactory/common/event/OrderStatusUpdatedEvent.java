package com.smartfactory.common.event;

import com.smartfactory.common.payloads.order_service.OrderStatusUpdatedPayload;

public record OrderStatusUpdatedEvent (DomainEvent<OrderStatusUpdatedPayload> event) {
}
