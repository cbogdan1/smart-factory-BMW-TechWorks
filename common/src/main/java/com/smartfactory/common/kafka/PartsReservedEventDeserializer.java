package com.smartfactory.common.kafka;

import com.smartfactory.common.event.PartsReservedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PartsReservedEventDeserializer
        extends ObjectMapperDeserializer<PartsReservedEvent> {

    public PartsReservedEventDeserializer() {
        super(PartsReservedEvent.class);
    }
}
