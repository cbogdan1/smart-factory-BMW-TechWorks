package com.smartfactory.common.kafka;

import com.smartfactory.common.event.OrderStatusUpdatedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrderStatusUpdatedEventDeserializer extends ObjectMapperDeserializer<OrderStatusUpdatedEvent> {

    public OrderStatusUpdatedEventDeserializer() {
        super(OrderStatusUpdatedEvent.class);
    }
}
