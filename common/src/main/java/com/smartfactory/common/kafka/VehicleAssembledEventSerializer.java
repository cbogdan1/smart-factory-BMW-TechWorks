package com.smartfactory.common.kafka;

import com.smartfactory.common.event.VehicleAssembledEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class VehicleAssembledEventSerializer
        extends ObjectMapperSerializer<VehicleAssembledEvent> {
}
