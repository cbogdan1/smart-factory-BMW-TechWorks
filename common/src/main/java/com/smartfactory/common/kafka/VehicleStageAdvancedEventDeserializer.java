package com.smartfactory.common.kafka;

import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class VehicleStageAdvancedEventDeserializer extends ObjectMapperDeserializer<VehicleStageAdvancedEvent> {
    public VehicleStageAdvancedEventDeserializer() {
        super(VehicleStageAdvancedEvent.class);
    }
}
