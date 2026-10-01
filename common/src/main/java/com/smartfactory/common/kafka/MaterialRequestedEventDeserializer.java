package com.smartfactory.common.kafka;

import com.smartfactory.common.event.MaterialRequestedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class MaterialRequestedEventDeserializer extends ObjectMapperDeserializer<MaterialRequestedEvent> {
    public MaterialRequestedEventDeserializer() {
        super(MaterialRequestedEvent.class);
    }
}