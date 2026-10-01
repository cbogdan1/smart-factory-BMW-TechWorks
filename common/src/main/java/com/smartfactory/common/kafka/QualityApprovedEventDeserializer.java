package com.smartfactory.common.kafka;

import com.smartfactory.common.event.QualityApprovedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class QualityApprovedEventDeserializer extends ObjectMapperDeserializer<QualityApprovedEvent> {
    public QualityApprovedEventDeserializer() {
        super(QualityApprovedEvent.class);
    }
}
