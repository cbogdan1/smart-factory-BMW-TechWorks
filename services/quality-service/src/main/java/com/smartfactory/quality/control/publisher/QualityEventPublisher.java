package com.smartfactory.quality.control.publisher;

import java.util.UUID;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.QualityApprovedEvent;
import com.smartfactory.common.event.QualityFailedEvent;
import com.smartfactory.common.event.QualityReworkRequiredEvent;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class QualityEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(QualityEventPublisher.class);
    private static final String SOURCE_SYSTEM = "quality-service";

    @Inject
    @Channel(Topics.QUALITY_APPROVED)
    Emitter<QualityApprovedEvent> approvedEmitter;

    @Inject
    @Channel(Topics.QUALITY_REWORK_REQUIRED)
    Emitter<QualityReworkRequiredEvent> reworkRequiredEmitter;

    @Inject
    @Channel(Topics.QUALITY_FAILED)
    Emitter<QualityFailedEvent> failedEmitter;

    public void publishApproved(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) final QualityApprovedPayload payload) {
        final DomainEvent<QualityApprovedPayload> domainEvent =
                new DomainEvent<>("EVT-" + UUID.randomUUID(), Topics.QUALITY_APPROVED, SOURCE_SYSTEM,
                        payload.vehicleId(), payload);

        LOG.info("Publishing {} [eventId={}, vehicleId={}, score={}]", Topics.QUALITY_APPROVED, domainEvent.eventId(),
                payload.vehicleId(), payload.score());

        approvedEmitter.send(Message.of(new QualityApprovedEvent(domainEvent)).addMetadata(keyMetadata(domainEvent)));
    }

    public void publishReworkRequired(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) final QualityReworkRequiredPayload payload) {
        final DomainEvent<QualityReworkRequiredPayload> domainEvent =
                new DomainEvent<>("EVT-" + UUID.randomUUID(), Topics.QUALITY_REWORK_REQUIRED, SOURCE_SYSTEM,
                        payload.vehicleId(), payload);

        LOG.info("Publishing {} [eventId={}, vehicleId={}, score={}, targetStage={}]", Topics.QUALITY_REWORK_REQUIRED,
                domainEvent.eventId(), payload.vehicleId(), payload.score(), payload.targetStage());

        reworkRequiredEmitter.send(
                Message.of(new QualityReworkRequiredEvent(domainEvent)).addMetadata(keyMetadata(domainEvent)));
    }

    public void publishFailed(@Observes(during = TransactionPhase.AFTER_SUCCESS) final QualityFailedPayload payload) {
        final DomainEvent<QualityFailedPayload> domainEvent =
                new DomainEvent<>("EVT-" + UUID.randomUUID(), Topics.QUALITY_FAILED, SOURCE_SYSTEM, payload.vehicleId(),
                        payload);

        LOG.info("Publishing {} [eventId={}, vehicleId={}, score={}, defectCodes={}]", Topics.QUALITY_FAILED,
                domainEvent.eventId(), payload.vehicleId(), payload.score(), payload.defectCodes());

        failedEmitter.send(Message.of(new QualityFailedEvent(domainEvent)).addMetadata(keyMetadata(domainEvent)));
    }

    private OutgoingKafkaRecordMetadata<String> keyMetadata(final DomainEvent<?> domainEvent) {
        return OutgoingKafkaRecordMetadata.<String>builder().withKey(domainEvent.kafkaKey()).build();
    }
}