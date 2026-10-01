package com.smartfactory.common.kafka;

import com.smartfactory.common.event.PartsDeliveredEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class PartsDeliveredEventSerializer extends ObjectMapperSerializer<PartsDeliveredEvent> {
}
