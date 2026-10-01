package com.smartfactory.planning.boundary.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.PlanningServiceExceptions;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import com.smartfactory.planning.control.service.ProductionPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleStageAdvancedConsumerTest {
    @Mock
    private ProductionPlanService productionPlanService;

    @InjectMocks
    private VehicleStageAdvancedConsumer consumer;

    private VehicleStageAdvancedEvent event;

    @BeforeEach
    void setUp() {
        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEH-000001", "ORD-000001", ProductionStage.CREATED,
                        ProductionStage.BODY, Instant.now());

        final DomainEvent<VehicleStageAdvancedPayload> domainEvent =
                new DomainEvent<>("EVT-000001", "vehicle-stage-advanced", "assembly-service", "VEH-000001", payload);

        event = new VehicleStageAdvancedEvent(domainEvent);
    }

    @Test
    void shouldRejectNullEvent() {
        final BusinessException exception =
                org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class, () -> consumer.consume(null));

        assertEquals(PlanningServiceExceptions.INVALID_VEHICLE_STAGE_ADVANCED_EVENT, exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(productionPlanService);
    }

    @Test
    void shouldRejectEventWithNullDomainEvent() {
        final VehicleStageAdvancedEvent invalidEvent = new VehicleStageAdvancedEvent(null);

        final BusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class,
                () -> consumer.consume(invalidEvent));

        assertEquals(PlanningServiceExceptions.INVALID_VEHICLE_STAGE_ADVANCED_EVENT, exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(productionPlanService);
    }
}