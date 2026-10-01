package com.smartfactory.common.kafka;

import com.smartfactory.common.event.ProductionPlannedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class ProductionPlannedEventSerializer
        extends ObjectMapperSerializer<ProductionPlannedEvent> {
}