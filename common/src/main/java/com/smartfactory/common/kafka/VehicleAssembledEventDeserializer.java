package com.smartfactory.common.kafka;

import com.smartfactory.common.event.VehicleAssembledEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class VehicleAssembledEventDeserializer extends ObjectMapperDeserializer<VehicleAssembledEvent> {
    public VehicleAssembledEventDeserializer() {
        super(VehicleAssembledEvent.class);
    }
}
