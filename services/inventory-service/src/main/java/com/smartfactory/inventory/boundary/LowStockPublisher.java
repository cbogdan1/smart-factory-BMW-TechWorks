package com.smartfactory.inventory.boundary;

import static com.smartfactory.common.Topics.INVENTORY_LOW_STOCK;

import java.time.Instant;
import java.util.UUID;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.InventoryLowStockEvent;
import com.smartfactory.common.payloads.inventory_service.InventoryLowStockPayload;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
@Slf4j
public class LowStockPublisher {

    @Inject
    @Channel(INVENTORY_LOW_STOCK)
    Emitter<Record<String, InventoryLowStockEvent>> lowStockEmitter;

    public void publishLowStock(final String partId, final String partCode, final long availableQuantity,
            final long minimumQuantity) {
        final InventoryLowStockPayload payload =
                new InventoryLowStockPayload(partId, partCode, availableQuantity, minimumQuantity,
                        Instant.now().toString());
        final DomainEvent<InventoryLowStockPayload> domainEvent =
                new DomainEvent<>("EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                        "inventory-low-stock", "inventory-service", partId, payload);
        final InventoryLowStockEvent event = new InventoryLowStockEvent(domainEvent);
        lowStockEmitter.send(Record.of(partId, event));
        log.warn("Low stock detected and published for part {} (partId: {}). Available quantity: {}, Minimum quantity: {}",
                partCode, partId, availableQuantity, minimumQuantity);
    }
}
