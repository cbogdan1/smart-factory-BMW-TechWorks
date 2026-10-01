package com.smartfactory.common.kafka;

import com.smartfactory.common.event.QualityReworkRequiredEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class QualityReworkRequiredEventDeserializer extends ObjectMapperDeserializer<QualityReworkRequiredEvent> {
    public QualityReworkRequiredEventDeserializer() {
        super(QualityReworkRequiredEvent.class);
    }
}
