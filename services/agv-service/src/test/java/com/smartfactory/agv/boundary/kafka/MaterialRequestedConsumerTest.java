package com.smartfactory.agv.boundary.kafka;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.smartfactory.agv.control.service.DeliveryMissionService;
import com.smartfactory.common.Topics;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.MaterialRequestedEvent;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class MaterialRequestedConsumerTest {

    @InjectMock
    DeliveryMissionService deliveryMissionService;

    @Inject
    MaterialRequestedConsumer consumer;

    @Test
    void shouldConsumeMaterialRequestAndExecuteMission() {
        final MaterialRequestedPayload payload =
                new MaterialRequestedPayload("REQ-001", "VEH-001", "ORD-001", "PAINT_KIT", 1, ProductionStage.PAINT,
                        "LINE-2-PAINT-STATION");

        final DomainEvent<MaterialRequestedPayload> domainEvent =
                new DomainEvent<>("EVT-001", Topics.MATERIAL_REQUESTED, "assembly-service", "VEH-001", payload);

        final MaterialRequestedEvent event = new MaterialRequestedEvent(domainEvent);

        consumer.consumeMaterialRequest(event);

        verify(deliveryMissionService, Mockito.times(1)).executeDeliveryMission(eq("EVT-001"), eq("material-requested"),
                eq(payload));
    }
}