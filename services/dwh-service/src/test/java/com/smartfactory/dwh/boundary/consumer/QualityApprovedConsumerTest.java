package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.QUALITY_APPROVED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityApprovedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QualityApprovedConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private QualityApprovedConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {

        final QualityApprovedEvent event = new QualityApprovedEvent(
                new DomainEvent<>("EVT-123", QUALITY_APPROVED, "quality-service", "VEH-001",
                        new QualityApprovedPayload("INSP-001", "VEH-001", 95, Instant.now())));

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
