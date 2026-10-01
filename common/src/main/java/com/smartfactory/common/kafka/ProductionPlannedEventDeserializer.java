package com.smartfactory.common.kafka;

import com.smartfactory.common.event.ProductionPlannedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ProductionPlannedEventDeserializer
        extends ObjectMapperDeserializer<ProductionPlannedEvent> {

    public ProductionPlannedEventDeserializer() {
        super(ProductionPlannedEvent.class);
    }
}