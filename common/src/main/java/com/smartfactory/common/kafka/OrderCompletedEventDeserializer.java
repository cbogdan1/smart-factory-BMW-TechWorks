package com.smartfactory.common.kafka;

import com.smartfactory.common.event.OrderCompletedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrderCompletedEventDeserializer extends ObjectMapperDeserializer<OrderCompletedEvent> {
    public OrderCompletedEventDeserializer() {
        super(OrderCompletedEvent.class);
    }
}
