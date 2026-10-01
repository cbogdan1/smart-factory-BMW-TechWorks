package com.smartfactory.common.kafka;

import com.smartfactory.common.event.PartsDeliveredEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PartsDeliveredEventDeserializer extends ObjectMapperDeserializer<PartsDeliveredEvent> {
    public PartsDeliveredEventDeserializer() {
        super(PartsDeliveredEvent.class);
    }
}
