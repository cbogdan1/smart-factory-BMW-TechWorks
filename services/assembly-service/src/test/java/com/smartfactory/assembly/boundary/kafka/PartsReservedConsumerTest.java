package com.smartfactory.assembly.boundary.kafka;

import static com.smartfactory.common.Topics.PARTS_RESERVED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import com.smartfactory.assembly.boundary.consumer.PartsReservedConsumer;
import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsReservedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.inventory_service.PartsReservedPayload;
import com.smartfactory.common.dto.inventory.ReservedPart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartsReservedConsumerTest {

    @Mock
    VehicleProductionService vehicleProductionService;

    @InjectMocks
    PartsReservedConsumer consumer;

    @Test
    void shouldDelegateValidEventToVehicleProductionService() {
        final String vehicleId = "VEH-DEMO-001";
        final String eventId = "EVT-DEMO-001";
        final PartsReservedEvent event = createEvent(eventId, vehicleId);

        final PartsReservedPayload payload = event.event().payload();

        consumer.consume(event);

        verify(vehicleProductionService).startProduction(eventId, payload);
    }

    @Test
    void shouldThrowBusinessExceptionWhenEventIsNull() {
        assertThrows(
                BusinessException.class,
                () -> consumer.consume(null));

        verifyNoInteractions(vehicleProductionService);
    }

    @Test
    void shouldThrowBusinessExceptionWhenEventIdIsMissing() {
        final PartsReservedEvent event =
                createEvent(null, "VEH-DEMO-001");

        assertThrows(
                BusinessException.class,
                () -> consumer.consume(event));

        verifyNoInteractions(vehicleProductionService);
    }

    @Test
    void shouldThrowBusinessExceptionWhenVehicleIdIsMissing() {
        final PartsReservedEvent event =
                createEvent("EVT-DEMO-001", null);

        assertThrows(
                BusinessException.class,
                () -> consumer.consume(event));

        verifyNoInteractions(vehicleProductionService);
    }

    @Test
    void shouldThrowBusinessExceptionWhenPayloadIsMissing() {
        final DomainEvent<PartsReservedPayload> domainEvent =
                new DomainEvent<>(
                        "EVT-DEMO-001",
                        PARTS_RESERVED,
                        "inventory-service",
                        "VEH-DEMO-001",
                        null);

        final PartsReservedEvent event =
                new PartsReservedEvent(domainEvent);

        assertThrows(
                BusinessException.class,
                () -> consumer.consume(event));

        verifyNoInteractions(vehicleProductionService);
    }

    private PartsReservedEvent createEvent(
            final String eventId,
            final String vehicleId) {

        final PartsReservedPayload payload =
                new PartsReservedPayload(
                        "RES-DEMO-001",
                        vehicleId,
                        "PLAN-DEMO-001",
                        List.of(new ReservedPart("PART-DEMO-001", 4)),
                        "2026-08-12T10:00:00Z");

        final DomainEvent<PartsReservedPayload> domainEvent =
                new DomainEvent<>(
                        eventId,
                        PARTS_RESERVED,
                        "inventory-service",
                        vehicleId,
                        payload);

        return new PartsReservedEvent(domainEvent);
    }
}