package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.MaterialDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import com.smartfactory.inventory.control.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaterialDeliveredConsumerTest {

    @Mock
    ReservationService reservationService;

    @InjectMocks
    MaterialDeliveredConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {
        final MaterialDeliveredPayload payload =
                new MaterialDeliveredPayload("MISS-1", "AGV-1", "VEH-1", "PART-1", 10, "NODE-1", Instant.now());
        final DomainEvent<MaterialDeliveredPayload> domainEvent =
                new DomainEvent<>("EVT", "type", "src", "key", payload);
        final MaterialDeliveredEvent event = new MaterialDeliveredEvent(domainEvent);

        consumer.consume(event);

        verify(reservationService).processMaterialDelivered(payload);
    }

    @Test
    void shouldThrowExceptionWhenEventIsNull() {
        final BusinessException ex = assertThrows(BusinessException.class, () -> consumer.consume(null));

        assertEquals("MATERIAL_DELIVERED_EVENT_INVALID", ex.getErrorCode());
        assertEquals(400, ex.getStatusCode());
        verifyNoInteractions(reservationService);
    }

    @Test
    void shouldThrowExceptionWhenDomainEventIsNull() {
        final BusinessException ex =
                assertThrows(BusinessException.class, () -> consumer.consume(new MaterialDeliveredEvent(null)));

        assertEquals("MATERIAL_DELIVERED_EVENT_INVALID", ex.getErrorCode());
        verifyNoInteractions(reservationService);
    }

    @Test
    void shouldThrowExceptionWhenPayloadIsNull() {
        final MaterialDeliveredEvent event = new MaterialDeliveredEvent(new DomainEvent<>("EVT", "t", "s", "k", null));

        final BusinessException ex = assertThrows(BusinessException.class, () -> consumer.consume(event));

        assertEquals("MATERIAL_DELIVERED_EVENT_INVALID", ex.getErrorCode());
        verifyNoInteractions(reservationService);
    }

    @Test
    void shouldCatchExceptionWithoutThrowing() {
        final MaterialDeliveredPayload payload =
                new MaterialDeliveredPayload("MISS-1", "AGV-1", "VEH-1", "PART-1", 10, "NODE-1", Instant.now());
        final DomainEvent<MaterialDeliveredPayload> domainEvent =
                new DomainEvent<>("EVT", "type", "src", "key", payload);
        final MaterialDeliveredEvent event = new MaterialDeliveredEvent(domainEvent);

        doThrow(new RuntimeException("Test")).when(reservationService).processMaterialDelivered(payload);

        consumer.consume(event);

        verify(reservationService).processMaterialDelivered(payload);
    }
}