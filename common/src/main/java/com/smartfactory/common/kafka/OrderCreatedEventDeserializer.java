package com.smartfactory.common.kafka;

import com.smartfactory.common.event.OrderCreatedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrderCreatedEventDeserializer extends ObjectMapperDeserializer<OrderCreatedEvent> {
    public OrderCreatedEventDeserializer() {
        super(OrderCreatedEvent.class);
    }
}