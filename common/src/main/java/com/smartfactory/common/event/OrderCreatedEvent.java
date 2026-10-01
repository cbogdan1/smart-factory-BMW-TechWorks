package com.smartfactory.common.event;

import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;

public record OrderCreatedEvent(DomainEvent<OrderCreatedPayload> event) {

}