package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.ORDER_CREATED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.OrderCreatedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderCreatedConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private OrderCreatedConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {
        final OrderCreatedEvent event = new OrderCreatedEvent(
                new DomainEvent<>("EVT-123", ORDER_CREATED, "Order-Management", "VEH-001",
                        new OrderCreatedPayload("ORD-001", "VEH-001", "John Doe", "john@email", "Model-X", "Blue",
                                "Long Range", Priority.HIGH, OrderStatus.CREATED)));

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