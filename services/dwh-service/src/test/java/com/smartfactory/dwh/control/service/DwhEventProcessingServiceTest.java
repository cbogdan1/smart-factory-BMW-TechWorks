package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.Topics.ORDER_CREATED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DwhEventProcessingServiceTest {
    @Mock
    private EventStoreService eventStoreService;

    @Mock
    private VehicleTimelineService vehicleTimelineService;

    @Mock
    private VehicleTwinService vehicleTwinService;

    @InjectMocks
    private DwhEventProcessingService dwhEventProcessingService;

    @Test
    void shouldProcessValidEvent() {

        final DomainEvent<OrderCreatedPayload> event =
                new DomainEvent<>("EVT-123", ORDER_CREATED, "order-service", "VEH-001",
                        new OrderCreatedPayload("ORD-001", "VEH-001", "John Doe", "john@email.com", "Model-X", "Blue",
                                "Long Range", Priority.HIGH, OrderStatus.CREATED));

        assertDoesNotThrow(() -> dwhEventProcessingService.processEvent(event));

        verify(vehicleTimelineService).saveTimelineEntry(event);
        verify(vehicleTwinService).processEvent(event);

        verify(eventStoreService).saveEvent(event);
    }

    @Test
    void shouldThrowForNullEvent() {

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> dwhEventProcessingService.processEvent(null));
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(vehicleTimelineService);
        verifyNoInteractions(vehicleTwinService);
        verifyNoInteractions(eventStoreService);
    }

    @Test
    void shouldThrowForMissingEventId() {

        final DomainEvent<OrderCreatedPayload> event =
                new DomainEvent<>(null, ORDER_CREATED, "order-service", "VEH-001",
                        new OrderCreatedPayload("ORD-001", "VEH-001", "John Doe", "john@email.com", "Model-X", "Blue",
                                "Long Range", Priority.HIGH, OrderStatus.CREATED));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> dwhEventProcessingService.processEvent(event));
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(vehicleTimelineService);
        verifyNoInteractions(vehicleTwinService);
        verifyNoInteractions(eventStoreService);
    }

    @Test
    void shouldThrowForMissingEventType() {

        final DomainEvent<OrderCreatedPayload> event = new DomainEvent<>("EVT-123", null, "order-service", "VEH-001",
                new OrderCreatedPayload("ORD-001", "VEH-001", "John Doe", "john@email.com", "Model-X", "Blue",
                        "Long Range", Priority.HIGH, OrderStatus.CREATED));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> dwhEventProcessingService.processEvent(event));
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(vehicleTimelineService);
        verifyNoInteractions(vehicleTwinService);
        verifyNoInteractions(eventStoreService);
    }
}
