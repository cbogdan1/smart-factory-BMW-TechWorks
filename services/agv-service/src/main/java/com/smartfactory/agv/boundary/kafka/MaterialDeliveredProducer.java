package com.smartfactory.agv.boundary.kafka;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.MaterialDeliveredEvent;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@Slf4j
@ApplicationScoped
public class MaterialDeliveredProducer {

    private static final String SOURCE_SYSTEM = "agv-service";

    @Channel(Topics.MATERIAL_DELIVERED)
    Emitter<MaterialDeliveredEvent> emitter;

    public CompletionStage<Void> publishMaterialDelivered(final MaterialDeliveredPayload payload) {

        final DomainEvent<MaterialDeliveredPayload> domainEvent =
                new DomainEvent<>("EVT-" + UUID.randomUUID(), Topics.MATERIAL_DELIVERED, SOURCE_SYSTEM,
                        payload.vehicleId(), payload);

        final MaterialDeliveredEvent event = new MaterialDeliveredEvent(domainEvent);

        final OutgoingKafkaRecordMetadata<String> metadata =
                OutgoingKafkaRecordMetadata.<String>builder().withKey(domainEvent.kafkaKey()).build();

        final CompletableFuture<Void> publicationResult = new CompletableFuture<>();

        final Message<MaterialDeliveredEvent> message = Message.of(event, () -> {
            log.info("Published material-delivered event: eventId={}, missionId={}", domainEvent.eventId(),
                    payload.missionId());

            publicationResult.complete(null);
            return CompletableFuture.completedFuture(null);
        }, failure -> {
            log.error("Failed to publish material-delivered event: eventId={}, missionId={}", domainEvent.eventId(),
                    payload.missionId(), failure);

            publicationResult.completeExceptionally(failure);
            return CompletableFuture.completedFuture(null);
        }).addMetadata(metadata);

        log.info("Publishing material-delivered event: eventId={}, missionId={}, agvId={}, vehicleId={}",
                domainEvent.eventId(), payload.missionId(), payload.agvId(), payload.vehicleId());

        emitter.send(message);

        return publicationResult;
    }
}