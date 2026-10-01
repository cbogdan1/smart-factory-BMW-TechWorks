package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.QUALITY_FAILED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;
import java.util.List;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityFailedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QualityFailedConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private QualityFailedConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {

        final QualityFailedEvent event = new QualityFailedEvent(
                new DomainEvent<>("EVT-123", QUALITY_FAILED, "quality-service", "VEH-001",
                        new QualityFailedPayload("INSP-001", "VEH-001", 40, List.of("PAINT_DEFECT"), Instant.now())));

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
