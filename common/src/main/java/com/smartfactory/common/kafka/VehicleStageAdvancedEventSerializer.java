package com.smartfactory.common.kafka;

import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperSerializer;

public class VehicleStageAdvancedEventSerializer extends ObjectMapperSerializer<VehicleStageAdvancedEvent> {
}
