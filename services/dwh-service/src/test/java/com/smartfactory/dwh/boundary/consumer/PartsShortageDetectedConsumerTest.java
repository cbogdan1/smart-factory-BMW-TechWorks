package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PARTS_SHORTAGE_DETECTED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsShortageDetectedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.procurement.PartsShortageDetectedPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PartsShortageDetectedConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private PartsShortageDetectedConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {

        final PartsShortageDetectedEvent event = new PartsShortageDetectedEvent(
                new DomainEvent<>("EVT-123", PARTS_SHORTAGE_DETECTED, "inventory-service", "VEH-001",
                        new PartsShortageDetectedPayload("VEH-001", "PLAN-001", "PART-001", "Battery Pack", 4, 1, 3)));

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
