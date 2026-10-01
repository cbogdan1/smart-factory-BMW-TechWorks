package com.smartfactory.assembly.boundary.kafka;

import static com.smartfactory.common.Topics.VEHICLE_ASSEMBLED;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.time.Instant;

import com.smartfactory.assembly.boundary.producer.VehicleAssembledProducer;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.VehicleAssembledEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleAssembledProducerTest {

    @Mock
    Emitter<VehicleAssembledEvent> emitter;

    @InjectMocks
    VehicleAssembledProducer producer;

    @Test
    void shouldPublishVehicleAssembledEvent() {
        final Instant assembledAt =
                Instant.parse("2026-08-01T14:00:00Z");

        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload(
                        "VEH-001",
                        "ORD-001",
                        "BMW_I4",
                        "LINE-2",
                        assembledAt);

        producer.publishVehicleAssembled(payload);

        final ArgumentCaptor<Message<VehicleAssembledEvent>> captor =
                ArgumentCaptor.forClass(Message.class);

        verify(emitter).send(captor.capture());

        final Message<VehicleAssembledEvent> sentMessage =
                captor.getValue();

        assertNotNull(sentMessage);

        final VehicleAssembledEvent sentEvent =
                sentMessage.getPayload();

        assertNotNull(sentEvent);
        assertNotNull(sentEvent.event());

        final DomainEvent<VehicleAssembledPayload> domainEvent =
                sentEvent.event();

        assertTrue(domainEvent.eventId().startsWith("EVT-"));
        assertEquals(VEHICLE_ASSEMBLED, domainEvent.eventType());
        assertEquals(DomainEvent.CURRENT_VERSION, domainEvent.eventVersion());
        assertEquals(assembledAt, domainEvent.timestamp());
        assertEquals("assembly-service", domainEvent.sourceSystem());
        assertEquals("VEH-001", domainEvent.correlationId());
        assertSame(payload, domainEvent.payload());

        assertEquals("VEH-001", payload.vehicleId());
        assertEquals("ORD-001", payload.orderId());
        assertEquals("BMW_I4", payload.vehicleModel());
        assertEquals("LINE-2", payload.productionLine());
        assertEquals(assembledAt, payload.assembledAt());

        final OutgoingKafkaRecordMetadata<?> metadata =
                sentMessage
                        .getMetadata(OutgoingKafkaRecordMetadata.class)
                        .orElseThrow();

        assertEquals("VEH-001", metadata.getKey());
    }

    @Test
    void shouldThrowBusinessExceptionWhenPayloadIsNull() {
        final BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> producer.publishVehicleAssembled(null));

        assertEquals(NULL_VEHICLE_ASSEMBLED_PAYLOAD_ERROR_MESSAGE, exception.getMessage());
    }
}