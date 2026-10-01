package com.smartfactory.inventory.boundary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.smartfactory.common.event.PartsShortageDetectedEvent;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortageEventPublisherTest {

    @Mock
    Emitter<Record<String, PartsShortageDetectedEvent>> shortageEmitter;

    @InjectMocks
    ShortageEventPublisher publisher;

    @Test
    void shouldPublishShortage() {
        publisher.publishShortage("VEH-1", "PLAN-1", "PART-1", "P-Name", 100, 40, 60);

        verify(shortageEmitter).send(any(Record.class));
    }
}