package com.smartfactory.common.kafka;

import com.smartfactory.common.event.PartsReservedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class PartsReservedEventSerializer extends ObjectMapperSerializer<PartsReservedEvent> {
    public PartsReservedEventSerializer() {
        super();
    }
}
