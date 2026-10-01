package com.smartfactory.assembly.boundary.kafka;

import static com.smartfactory.common.Topics.PRODUCTION_PLANNED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_PRODUCTION_PLANNED_EVENT_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import com.smartfactory.assembly.boundary.consumer.ProductionPlannedEventConsumer;
import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.ProductionPlannedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductionPlannedEventConsumerTest {

    @Mock
    VehicleProductionService service;

    @InjectMocks
    ProductionPlannedEventConsumer consumer;

    @Test
    void shouldConsumeProductionPlannedEventAndCreateVehicleProduction() {
        final ProductionPlannedPayload payload =
                new ProductionPlannedPayload("PLAN-1", "ORD-1", "VEH-1", "MODEL-X", "Line-1", Priority.HIGH,
                        LocalDate.of(2026, 8, 11));

        final DomainEvent<ProductionPlannedPayload> domainEvent =
                new DomainEvent<>("EVT-1", PRODUCTION_PLANNED, "planning-service", "VEH-1", payload);

        final ProductionPlannedEvent event = new ProductionPlannedEvent(domainEvent);

        consumer.consume(event);

        verify(service).create(
                argThat((CreateVehicleProductionRequest request) -> request != null && request.vehicleId()
                        .equals("VEH-1") && request.orderId().equals("ORD-1") && request.vehicleModel()
                        .equals("MODEL-X") && request.productionLine().equals("Line-1")));
    }

    @Test
    void shouldThrowBusinessExceptionWhenEventIsNull() {
        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume(null));

        assertEquals(NULL_PRODUCTION_PLANNED_EVENT_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void shouldThrowBusinessExceptionWhenDomainEventIsNull() {
        final ProductionPlannedEvent event = new ProductionPlannedEvent(null);

        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume(event));

        assertEquals(NULL_PRODUCTION_PLANNED_EVENT_ERROR_MESSAGE, exception.getMessage());
    }
}