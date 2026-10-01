package com.smartfactory.common.kafka;

import com.smartfactory.common.event.PartsShortageDetectedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PartsShortageDetectedEventDeserializer extends ObjectMapperDeserializer<PartsShortageDetectedEvent> {

    public PartsShortageDetectedEventDeserializer() {
        super(PartsShortageDetectedEvent.class);
    }
}
