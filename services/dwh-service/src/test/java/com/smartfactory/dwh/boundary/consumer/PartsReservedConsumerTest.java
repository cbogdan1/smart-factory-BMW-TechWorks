package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PARTS_RESERVED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsReservedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PartsReservedConsumerTest {
    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private PartsReservedConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {

        final PartsReservedEvent event = new PartsReservedEvent(
                new DomainEvent<>("EVT-123", PARTS_RESERVED, "inventory-service", "VEH-001",
                        new PartsReservedPayload("RES-001", "VEH-001", "PLAN-001", List.of(), "2026-08-01T10:00:00Z")));

        assertDoesNotThrow(() -> consumer.consume(event));

        verify(dwhEventProcessingService).processEvent(event.event());
    }

    @Test
    void shouldHandleNullEvent() {
        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume((null)));
        assertEquals(400, exception.getStatusCode());
        verifyNoInteractions(dwhEventProcessingService);
    }
}
