package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.smartfactory.common.dto.inventory.ReservationResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsReservedEvent;
import com.smartfactory.common.event.ProductionPlannedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.inventory.control.MaterialDemandService;
import com.smartfactory.inventory.control.ReservationService;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Emitter;

class MaterialDemandConsumerTest {

    @InjectMocks
    MaterialDemandConsumer consumer;

    @Mock
    MaterialDemandService materialDemandService;

    @Mock
    ReservationService reservationService;

    @Mock
    Emitter<Record<String, PartsReservedEvent>> partsReservedEmitter;

    @Mock
    ProductionPlannedEvent productionPlannedEvent;

    @Mock
    DomainEvent<ProductionPlannedPayload> domainEvent;

    @Mock
    ProductionPlannedPayload payload;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenEventIsNull() {

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> consumer.consumeProductionPlanned(null)
        );

        assertEquals("PRODUCITON_PLANNED_EVENT_NULL", exception.getErrorCode());

        verifyNoInteractions(materialDemandService);
        verifyNoInteractions(reservationService);
    }


    @Test
    void shouldThrowExceptionWhenDomainEventIsNull() {

        when(productionPlannedEvent.event()).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> consumer.consumeProductionPlanned(productionPlannedEvent)
        );

        assertEquals("PRODUCITON_PLANNED_EVENT_NULL", exception.getErrorCode());

        verifyNoInteractions(materialDemandService);
        verifyNoInteractions(reservationService);
    }


    @Test
    void shouldThrowExceptionWhenEventIdIsNull() {

        when(productionPlannedEvent.event()).thenReturn(domainEvent);
        when(domainEvent.eventId()).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> consumer.consumeProductionPlanned(productionPlannedEvent)
        );

        assertEquals("PRODUCITON_PLANNED_EVENT_NULL", exception.getErrorCode());

        verifyNoInteractions(materialDemandService);
        verifyNoInteractions(reservationService);
    }


    @Test
    void shouldSkipReservationWhenNoDemandsWereCreated() {

        String eventId = "EVT-123";

        when(productionPlannedEvent.event()).thenReturn(domainEvent);
        when(domainEvent.eventId()).thenReturn(eventId);
        when(domainEvent.payload()).thenReturn(payload);

        when(materialDemandService.recordDemand(payload, eventId))
                .thenReturn(false);

        consumer.consumeProductionPlanned(productionPlannedEvent);

        verify(materialDemandService)
                .recordDemand(payload, eventId);

        verifyNoInteractions(reservationService);
        verifyNoInteractions(partsReservedEmitter);
    }


    @Test
    void shouldReservePartsWhenDemandsWereCreated() {

        String eventId = "EVT-123";

        when(productionPlannedEvent.event()).thenReturn(domainEvent);
        when(domainEvent.eventId()).thenReturn(eventId);
        when(domainEvent.payload()).thenReturn(payload);

        when(materialDemandService.recordDemand(payload, eventId))
                .thenReturn(true);

        when(reservationService.reserveMaterialForDemand(eventId))
                .thenReturn(List.of());

        consumer.consumeProductionPlanned(productionPlannedEvent);

        verify(materialDemandService)
                .recordDemand(payload, eventId);

        verify(reservationService)
                .reserveMaterialForDemand(eventId);

        verifyNoInteractions(partsReservedEmitter);
    }


    @Test
    void shouldCreateAndSendPartsReservedEventForEachReservation() {

        String eventId = "EVT-123";

        when(productionPlannedEvent.event()).thenReturn(domainEvent);
        when(domainEvent.eventId()).thenReturn(eventId);
        when(domainEvent.payload()).thenReturn(payload);

        when(payload.planId()).thenReturn("PLAN-123");

        when(materialDemandService.recordDemand(payload, eventId))
                .thenReturn(true);

        ReservationResponse reservation =
                mock(ReservationResponse.class);

        when(reservation.getReservationId())
                .thenReturn("RES-123");

        when(reservation.getVehicleId())
                .thenReturn("VEH-123");

        when(reservation.getPartCode())
                .thenReturn("PART-001");

        when(reservation.getQuantity())
                .thenReturn(5L);

        when(reservationService.reserveMaterialForDemand(eventId))
                .thenReturn(List.of(reservation));

        consumer.consumeProductionPlanned(productionPlannedEvent);

        verify(reservationService)
                .reserveMaterialForDemand(eventId);

        verify(partsReservedEmitter)
                .send(any(Record.class));
    }

    @Test
    void shouldNotPublishPartsReservedEventWhenReservationReturnsEmptyListDueToShortage() {
        String eventId = "EVT-123";

        when(productionPlannedEvent.event()).thenReturn(domainEvent);
        when(domainEvent.eventId()).thenReturn(eventId);
        when(domainEvent.payload()).thenReturn(payload);

        when(materialDemandService.recordDemand(payload, eventId))
                .thenReturn(true);

        when(reservationService.reserveMaterialForDemand(eventId))
                .thenReturn(List.of());

        assertDoesNotThrow(() ->
                consumer.consumeProductionPlanned(productionPlannedEvent)
        );

        verify(materialDemandService)
                .recordDemand(payload, eventId);

        verify(reservationService)
                .reserveMaterialForDemand(eventId);

        verifyNoInteractions(partsReservedEmitter);
    }
}