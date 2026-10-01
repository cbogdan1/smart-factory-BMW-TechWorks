package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.MATERIAL_DELIVERED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.MaterialDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MaterialDeliveredConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private MaterialDeliveredConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {

        final MaterialDeliveredEvent event = new MaterialDeliveredEvent(
                new DomainEvent<>("EVT-123", MATERIAL_DELIVERED, "agv-service", "VEH-001",
                        new MaterialDeliveredPayload("MIS-001", "AGV-001", "VEH-001", "Battery Pack", 4, "NODE-A",
                                Instant.now())));

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
