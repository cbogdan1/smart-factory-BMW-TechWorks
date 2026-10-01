package com.smartfactory.assembly.boundary.kafka;

import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import com.smartfactory.assembly.boundary.producer.MaterialRequestProducer;
import com.smartfactory.common.event.MaterialRequestedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaterialRequestProducerTest {

    @Mock
    Emitter<MaterialRequestedEvent> emitter;

    @InjectMocks
    MaterialRequestProducer producer;

    @Test
    void shouldPublishMaterialRequestedEvent() {
        final MaterialRequestedPayload payload =
                new MaterialRequestedPayload("REQ-1", "VEH-1", "ORDER-1", "PAINT_KIT", 1, PAINT,
                        "Line-1-PAINT-STATION");

        when(emitter.send(any(MaterialRequestedEvent.class))).thenReturn(CompletableFuture.completedFuture(null));

        producer.publishMaterialRequest(payload);

        final ArgumentCaptor<MaterialRequestedEvent> captor = ArgumentCaptor.forClass(MaterialRequestedEvent.class);

        verify(emitter).send(captor.capture());

        final MaterialRequestedEvent sentEvent = captor.getValue();

        assertNotNull(sentEvent);
        assertNotNull(sentEvent.event());

        assertNotNull(sentEvent.event().eventId());
        assertEquals("VEH-1", sentEvent.event().correlationId());

        assertNotNull(sentEvent.event().payload());
        assertEquals("REQ-1", sentEvent.event().payload().requestId());
        assertEquals("VEH-1", sentEvent.event().payload().vehicleId());
        assertEquals("ORDER-1", sentEvent.event().payload().orderId());
        assertEquals("PAINT_KIT", sentEvent.event().payload().material());
        assertEquals(1, sentEvent.event().payload().quantity());
        assertEquals(PAINT, sentEvent.event().payload().targetStage());
        assertEquals("Line-1-PAINT-STATION", sentEvent.event().payload().targetNode());
    }

    @Test
    void shouldThrowBusinessExceptionWhenPayloadIsNull() {
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> producer.publishMaterialRequest(null));

        assertEquals(NULL_MATERIAL_REQUESTED_PAYLOAD_ERROR_MESSAGE, exception.getMessage());
    }
}