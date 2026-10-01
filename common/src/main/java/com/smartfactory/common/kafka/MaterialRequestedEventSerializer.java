package com.smartfactory.common.kafka;

import com.smartfactory.common.event.MaterialRequestedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class MaterialRequestedEventSerializer extends ObjectMapperSerializer<MaterialRequestedEvent> {
}
