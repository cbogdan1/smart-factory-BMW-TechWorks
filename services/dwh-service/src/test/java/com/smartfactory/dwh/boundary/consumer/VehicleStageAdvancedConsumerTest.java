package com.smartfactory.dwh.boundary.consumer;

import static com.smartfactory.common.Topics.VEHICLE_STAGE_ADVANCED;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import com.smartfactory.dwh.control.service.DwhEventProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VehicleStageAdvancedConsumerTest {

    @Mock
    private DwhEventProcessingService dwhEventProcessingService;

    @InjectMocks
    private VehicleStageAdvancedConsumer consumer;

    @Test
    void shouldConsumeValidEvent() {

        final VehicleStageAdvancedEvent event = new VehicleStageAdvancedEvent(
                new DomainEvent<>("EVT-123", VEHICLE_STAGE_ADVANCED, "assembly-service", "VEH-001",
                        new VehicleStageAdvancedPayload("VEH-001", "ORD-001", ProductionStage.ASSEMBLED,
                                ProductionStage.BODY, Instant.now())));

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
