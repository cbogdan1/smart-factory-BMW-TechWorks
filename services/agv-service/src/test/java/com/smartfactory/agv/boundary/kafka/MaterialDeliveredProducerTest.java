package com.smartfactory.agv.boundary.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.MaterialDeliveredEvent;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaterialDeliveredProducerTest {

    @Mock
    Emitter<MaterialDeliveredEvent> emitter;

    @InjectMocks
    MaterialDeliveredProducer producer;

    @Test
    void shouldPublishMaterialDeliveredEvent() {
        // Arrange
        final MaterialDeliveredPayload payload =
                new MaterialDeliveredPayload("MIS-123", "AGV-001", "VEH-001", "PAINT_KIT", 1, "LINE-2-PAINT-STATION",
                        Instant.now());

        // Act
        producer.publishMaterialDelivered(payload);

        // Assert
        final ArgumentCaptor<Message<MaterialDeliveredEvent>> captor = ArgumentCaptor.forClass(Message.class);
        verify(emitter).send(captor.capture());

        final Message<MaterialDeliveredEvent> sentMessage = captor.getValue();
        assertNotNull(sentMessage);

        final MaterialDeliveredEvent sentEvent = sentMessage.getPayload();
        assertNotNull(sentEvent);
        assertNotNull(sentEvent.event());

        assertEquals("agv-service", sentEvent.event().sourceSystem());
        assertEquals(Topics.MATERIAL_DELIVERED, sentEvent.event().eventType());
        assertTrue(sentEvent.event().eventId().startsWith("EVT-"));
        assertEquals("VEH-001", sentEvent.event().correlationId());

        final MaterialDeliveredPayload sentPayload = sentEvent.event().payload();
        assertEquals("MIS-123", sentPayload.missionId());
        assertEquals("AGV-001", sentPayload.agvId());
        assertEquals("VEH-001", sentPayload.vehicleId());
        assertEquals("PAINT_KIT", sentPayload.material());
        assertEquals(1, sentPayload.quantity());
        assertEquals("LINE-2-PAINT-STATION", sentPayload.targetNode());
    }
}