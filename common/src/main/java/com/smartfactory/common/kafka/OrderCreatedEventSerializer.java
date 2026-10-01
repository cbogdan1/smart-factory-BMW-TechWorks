package com.smartfactory.common.kafka;

import com.smartfactory.common.event.OrderCreatedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class OrderCreatedEventSerializer extends ObjectMapperSerializer<OrderCreatedEvent> {
}
