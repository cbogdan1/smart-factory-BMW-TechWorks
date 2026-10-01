package com.smartfactory.inventory.boundary;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.smartfactory.common.event.InventoryLowStockEvent;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LowStockPublisherTest {

    @Mock
    Emitter<Record<String, InventoryLowStockEvent>> lowStockEmitter;

    @InjectMocks
    LowStockPublisher lowStockPublisher;

    @Test
    void shouldPublishLowStock() {
        lowStockPublisher.publishLowStock("PART-001", "P-CODE", 10, 50);

        verify(lowStockEmitter).send(any(Record.class));
    }
}