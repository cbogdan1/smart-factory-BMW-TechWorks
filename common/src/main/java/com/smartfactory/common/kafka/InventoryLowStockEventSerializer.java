package com.smartfactory.common.kafka;

import com.smartfactory.common.event.InventoryLowStockEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class InventoryLowStockEventSerializer extends ObjectMapperSerializer<InventoryLowStockEvent> {
    public InventoryLowStockEventSerializer() {
        super();
    }
}
