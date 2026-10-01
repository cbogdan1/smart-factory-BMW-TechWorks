package com.smartfactory.common.kafka;

import com.smartfactory.common.event.OrderStatusUpdatedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class OrderStatusUpdatedEventSerializer extends ObjectMapperSerializer<OrderStatusUpdatedEvent> {
}
