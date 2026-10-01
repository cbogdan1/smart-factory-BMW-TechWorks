package com.smartfactory.assembly.boundary.kafka;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import com.smartfactory.assembly.boundary.consumer.ProductionPlannedEventDlqConsumer;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;

class ProductionPlannedEventDlqConsumerTest {

    private final ProductionPlannedEventDlqConsumer consumer = new ProductionPlannedEventDlqConsumer();

    @Test
    void shouldAckDlqMessage() {
        final AtomicBoolean acked = new AtomicBoolean(false);

        final Message<String> message = Message.of("{\"message\":\"Failed production planned event\"}", () -> {
            acked.set(true);
            return CompletableFuture.completedFuture(null);
        });

        consumer.consume(message).toCompletableFuture().join();

        assertTrue(acked.get());
    }
}