package com.smartfactory.common.kafka;

import com.smartfactory.common.event.OrderCompletedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class OrderCompletedEventSerializer extends ObjectMapperSerializer<OrderCompletedEvent> {
}
