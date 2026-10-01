package com.smartfactory.planning.boundary.kafka;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.planning.control.service.ProductionPlanService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class VehicleAssembledConsumerTest {
    @Inject
    VehicleAssembledConsumer consumer;

    @InjectMock
    ProductionPlanService productionPlanService;

    @Test
    void consume_shouldCallCompletePlan() {
        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEHICLE-001", "ORDER-001", "BMW-M5", "LINE-01", Instant.now());

        final DomainEvent<VehicleAssembledPayload> event =
                new DomainEvent<>("EVT-001", "vehicle-assembled", "assembly-service", "VEHICLE-001", payload);

        final VehicleAssembledEvent vehicleAssembledEvent = new VehicleAssembledEvent(event);

        consumer.consume(vehicleAssembledEvent);

        verify(productionPlanService).completePlan("EVT-001", payload);
    }

    @Test
    void consume_shouldNotCallCompletePlanWithWrongEventId() {
        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEHICLE-001", "ORDER-001", "BMW-M5", "LINE-01", Instant.now());

        final DomainEvent<VehicleAssembledPayload> event =
                new DomainEvent<>("EVT-002", "vehicle-assembled", "assembly-service", "VEHICLE-001", payload);

        final VehicleAssembledEvent vehicleAssembledEvent = new VehicleAssembledEvent(event);

        consumer.consume(vehicleAssembledEvent);

        verify(productionPlanService).completePlan(eq("EVT-002"), eq(payload));
        verify(productionPlanService, never()).completePlan(eq("EVT-001"), any());
    }
}