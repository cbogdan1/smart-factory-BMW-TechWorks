package com.smartfactory.common.kafka;

import com.smartfactory.common.event.MaterialDeliveredEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class MaterialDeliveredEventSerializer extends ObjectMapperSerializer<MaterialDeliveredEvent> {
}