package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;
import com.smartfactory.inventory.control.InventoryStockService;
import com.smartfactory.inventory.entity.InventoryStock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartsDeliveredConsumerTest {

    @Mock
    InventoryStockService inventoryStockService;

    @InjectMocks
    PartsDeliveredConsumer consumer;

    @Test
    void shouldConsumePartsDelivered() {
        PartsDeliveredPayload payload = new PartsDeliveredPayload(
                "PO-123",
                "SUPP-1",
                "PART-1",
                "P-CODE",
                100,
                Instant.now()
        );
        DomainEvent<PartsDeliveredPayload> domainEvent = new DomainEvent<>("EVT-1", "type", "src", "key", payload);
        PartsDeliveredEvent event = new PartsDeliveredEvent(domainEvent);

        InventoryStock mockStock = new InventoryStock();
        when(inventoryStockService.processPartsDelivered("EVT-1", payload)).thenReturn(mockStock);

        consumer.consumePartsDelivered(event);

        verify(inventoryStockService).processPartsDelivered("EVT-1", payload);
        verify(inventoryStockService).checkAndPublishLowStock(mockStock);
    }

    @Test
    void shouldThrowExceptionForInvalidEvent() {
        assertThrows(BusinessException.class, () -> consumer.consumePartsDelivered(null));
        assertThrows(BusinessException.class, () -> consumer.consumePartsDelivered(new PartsDeliveredEvent(null)));
    }
}