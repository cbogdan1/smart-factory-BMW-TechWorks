package com.smartfactory.quality.control.publisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;
import java.util.List;

import com.smartfactory.common.Topics;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.event.QualityApprovedEvent;
import com.smartfactory.common.event.QualityFailedEvent;
import com.smartfactory.common.event.QualityReworkRequiredEvent;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
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
public class QualityEventPublisherTest {
    private static final Instant DECIDED_AT = Instant.parse("2026-08-01T14:10:00Z");

    @Mock
    Emitter<QualityApprovedEvent> approvedEmitter;

    @Mock
    Emitter<QualityReworkRequiredEvent> reworkRequiredEmitter;

    @Mock
    Emitter<QualityFailedEvent> failedEmitter;

    @InjectMocks
    QualityEventPublisher qualityEventPublisher;

    @Test
    public void publishApprovedSendsOneEventOnTheApprovedChannelOnly() {
        final QualityApprovedPayload payload = new QualityApprovedPayload("INSP-001", "VEH-001", 85, DECIDED_AT);

        qualityEventPublisher.publishApproved(payload);

        @SuppressWarnings("unchecked") final ArgumentCaptor<Message<QualityApprovedEvent>> captor =
                ArgumentCaptor.forClass((Class) Message.class);
        verify(approvedEmitter).send(captor.capture());

        verifyNoInteractions(reworkRequiredEmitter, failedEmitter);

        final QualityApprovedEvent sent = captor.getValue().getPayload();

        assertNotNull(sent.event().eventId());
        assertEquals(Topics.QUALITY_APPROVED, sent.event().eventType());
        assertEquals("1.0", sent.event().eventVersion());
        assertEquals("quality-service", sent.event().sourceSystem());
        assertEquals("VEH-001", sent.event().correlationId());
        assertEquals(payload, sent.event().payload());
    }

    @Test
    public void publishApprovedSetsTheVehicleIdAsTheKafkaKey() {
        qualityEventPublisher.publishApproved(new QualityApprovedPayload("INSP-001", "VEH-001", 85, DECIDED_AT));

        @SuppressWarnings("unchecked") final ArgumentCaptor<Message<QualityApprovedEvent>> captor =
                ArgumentCaptor.forClass((Class) Message.class);
        verify(approvedEmitter).send(captor.capture());

        final OutgoingKafkaRecordMetadata<?> metadata =
                captor.getValue().getMetadata(OutgoingKafkaRecordMetadata.class).orElseThrow();

        assertEquals("VEH-001", metadata.getKey());
    }

    @Test
    public void publishReworkRequiredSendsOneEventOnTheReworkChannelOnly() {
        final QualityReworkRequiredPayload payload =
                new QualityReworkRequiredPayload("INSP-002", "VEH-002", 72, ProductionStage.PAINT,
                        "PAINT_SCRATCH detected", DECIDED_AT);

        qualityEventPublisher.publishReworkRequired(payload);

        @SuppressWarnings("unchecked") final ArgumentCaptor<Message<QualityReworkRequiredEvent>> captor =
                ArgumentCaptor.forClass((Class) Message.class);
        verify(reworkRequiredEmitter).send(captor.capture());
        verifyNoInteractions(approvedEmitter, failedEmitter);

        final QualityReworkRequiredEvent sent = captor.getValue().getPayload();

        assertEquals(Topics.QUALITY_REWORK_REQUIRED, sent.event().eventType());
        assertEquals("VEH-002", sent.event().correlationId());
        assertEquals(ProductionStage.PAINT, sent.event().payload().targetStage());
        assertEquals("PAINT_SCRATCH detected", sent.event().payload().reason());
    }

    @Test
    public void publishFailedSendsOneEventOnTheFailedChannelOnly() {
        final QualityFailedPayload payload =
                new QualityFailedPayload("INSP-003", "VEH-003", 45, List.of("BATTERY_FAILURE", "MISSING_SEAT"),
                        DECIDED_AT);

        qualityEventPublisher.publishFailed(payload);

        @SuppressWarnings("unchecked") final ArgumentCaptor<Message<QualityFailedEvent>> captor =
                ArgumentCaptor.forClass((Class) Message.class);
        verify(failedEmitter).send(captor.capture());
        verifyNoInteractions(approvedEmitter, reworkRequiredEmitter);

        final QualityFailedEvent sent = captor.getValue().getPayload();

        assertEquals(Topics.QUALITY_FAILED, sent.event().eventType());
        assertEquals(List.of("BATTERY_FAILURE", "MISSING_SEAT"), sent.event().payload().defectCodes());
    }
}