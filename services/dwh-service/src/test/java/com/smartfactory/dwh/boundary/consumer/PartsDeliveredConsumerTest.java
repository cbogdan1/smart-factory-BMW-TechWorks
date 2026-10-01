package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PARTS_DELIVERED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PartsDeliveredConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private PartsDeliveredConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {

        final PartsDeliveredEvent event = new PartsDeliveredEvent(
                new DomainEvent<>("EVT-123", PARTS_DELIVERED, "procurement-service", "VEH-001",
                        new PartsDeliveredPayload("PO-001", "SUP-001", "PART-001", "BATTERY-001", 100, Instant.now())));

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
