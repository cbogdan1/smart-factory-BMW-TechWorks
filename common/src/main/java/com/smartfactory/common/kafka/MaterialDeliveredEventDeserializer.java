package com.smartfactory.common.kafka;

import com.smartfactory.common.event.MaterialDeliveredEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class MaterialDeliveredEventDeserializer extends ObjectMapperDeserializer<MaterialDeliveredEvent> {
    public MaterialDeliveredEventDeserializer() {
        super(MaterialDeliveredEvent.class);
    }
}
