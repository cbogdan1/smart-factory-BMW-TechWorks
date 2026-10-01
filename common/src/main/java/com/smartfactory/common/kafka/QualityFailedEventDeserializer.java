package com.smartfactory.common.kafka;

import com.smartfactory.common.event.QualityFailedEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class QualityFailedEventDeserializer extends ObjectMapperDeserializer<QualityFailedEvent> {
    public QualityFailedEventDeserializer() {
        super(QualityFailedEvent.class);
    }
}
