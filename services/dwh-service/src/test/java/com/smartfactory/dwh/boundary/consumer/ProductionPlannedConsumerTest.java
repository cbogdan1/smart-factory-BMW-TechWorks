package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.PRODUCTION_PLANNED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;

import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.ProductionPlannedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductionPlannedConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private ProductionPlannedConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {
        final ProductionPlannedEvent event = new ProductionPlannedEvent(
                new DomainEvent<>("EVT-123", PRODUCTION_PLANNED, "Production-Planning", "VEH-001",
                        new ProductionPlannedPayload("PLAN-001", "ORD-001", "VEH-001", "BMW_I4", "LINE-A",
                                Priority.HIGH, LocalDate.of(2026, 8, 1))));
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