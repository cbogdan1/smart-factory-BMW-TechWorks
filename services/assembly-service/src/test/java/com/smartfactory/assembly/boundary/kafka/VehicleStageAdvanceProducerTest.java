package com.smartfactory.assembly.boundary.kafka;

import static com.smartfactory.common.Topics.VEHICLE_STAGE_ADVANCED;
import static com.smartfactory.common.enums.ProductionStage.BODY;
import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import com.smartfactory.assembly.boundary.producer.VehicleStageAdvanceProducer;
import com.smartfactory.common.event.VehicleStageAdvancedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleStageAdvanceProducerTest {

    @Mock
    Emitter<VehicleStageAdvancedEvent> emitter;

    @InjectMocks
    VehicleStageAdvanceProducer producer;

    @Test
    void shouldPublishVehicleStageAdvancedEvent() {
        final Instant advancedAt = Instant.parse("2026-08-01T11:05:00Z");

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEH-1", "ORDER-1", BODY, PAINT, advancedAt);

        when(emitter.send(any(VehicleStageAdvancedEvent.class))).thenReturn(CompletableFuture.completedFuture(null));

        producer.publishAdvanceRequest(payload);

        final ArgumentCaptor<VehicleStageAdvancedEvent> captor =
                ArgumentCaptor.forClass(VehicleStageAdvancedEvent.class);

        verify(emitter).send(captor.capture());

        final VehicleStageAdvancedEvent sentEvent = captor.getValue();

        assertNotNull(sentEvent);
        assertNotNull(sentEvent.event());

        assertNotNull(sentEvent.event().eventId());
        assertEquals(VEHICLE_STAGE_ADVANCED, sentEvent.event().eventType());
        assertEquals("assembly-service", sentEvent.event().sourceSystem());
        assertEquals("VEH-1", sentEvent.event().correlationId());

        assertNotNull(sentEvent.event().payload());
        assertEquals("VEH-1", sentEvent.event().payload().vehicleId());
        assertEquals("ORDER-1", sentEvent.event().payload().orderId());
        assertEquals(BODY, sentEvent.event().payload().previousStage());
        assertEquals(PAINT, sentEvent.event().payload().currentStage());
        assertEquals(advancedAt, sentEvent.event().payload().advancedAt());
    }

    @Test
    void shouldThrowBusinessExceptionWhenPayloadIsNull() {
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> producer.publishAdvanceRequest(null));

        assertEquals(NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE, exception.getMessage());
    }
}