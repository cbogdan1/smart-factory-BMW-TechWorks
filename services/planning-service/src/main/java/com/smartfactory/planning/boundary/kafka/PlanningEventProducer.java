package com.smartfactory.planning.boundary.kafka;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PlanningKpiUpdatedEvent;
import com.smartfactory.common.event.ProductionPlannedEvent;
import com.smartfactory.common.payloads.planning_service.PlanningKpiUpdatedPayload;
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

@Slf4j
@ApplicationScoped
public class PlanningEventProducer {
    private static final String SOURCE_SYSTEM = "planning-service";

    @Channel(Topics.PRODUCTION_PLANNED)
    Emitter<ProductionPlannedEvent> productionPlannedEventEmitter;
    @Channel(Topics.PLANNING_KPI_UPDATED)
    Emitter<PlanningKpiUpdatedEvent> planningKpiUpdatedEventEmitter;

    public CompletionStage<Void> publishProductionPlanned(final ProductionPlannedPayload payload) {
        final DomainEvent<ProductionPlannedPayload> domainEvent =
                new DomainEvent<>("EVT-" + UUID.randomUUID(), Topics.PRODUCTION_PLANNED, SOURCE_SYSTEM,
                        payload.vehicleId(), payload);

        final ProductionPlannedEvent event = new ProductionPlannedEvent(domainEvent);

        final OutgoingKafkaRecordMetadata<String> metadata =
                OutgoingKafkaRecordMetadata.<String>builder().withKey(domainEvent.kafkaKey()).build();

        final CompletableFuture<Void> publicationResult = new CompletableFuture<>();

        final Message<ProductionPlannedEvent> message = Message.of(event, () -> {
            log.info("Published production-planned event: eventId={}, planId={}", domainEvent.eventId(),
                    payload.planId());

            publicationResult.complete(null);
            return CompletableFuture.completedFuture(null);
        }, failure -> {
            log.error("Failed to publish production-planned event: eventId={}, planId={}", domainEvent.eventId(),
                    payload.planId(), failure);

            publicationResult.completeExceptionally(failure);
            return CompletableFuture.completedFuture(null);
        }).addMetadata(metadata);

        log.info("Publishing production-planned event: eventId={}, planId={}, orderId={}, vehicleId={}",
                domainEvent.eventId(), payload.planId(), payload.orderId(), payload.vehicleId());

        productionPlannedEventEmitter.send(message);
        return publicationResult;
    }

    public CompletionStage<Void> publishPlanningKpiUpdated(final PlanningKpiUpdatedPayload payload) {
        final DomainEvent<PlanningKpiUpdatedPayload> domainEvent =
                new DomainEvent<>("EVT-" + UUID.randomUUID(), Topics.PLANNING_KPI_UPDATED, SOURCE_SYSTEM,
                        payload.productionLine(), payload);

        final PlanningKpiUpdatedEvent event = new PlanningKpiUpdatedEvent(domainEvent);

        final OutgoingKafkaRecordMetadata<String> metadata =
                OutgoingKafkaRecordMetadata.<String>builder().withKey(domainEvent.kafkaKey()).build();

        final CompletableFuture<Void> publicationResult = new CompletableFuture<>();

        final Message<PlanningKpiUpdatedEvent> message = Message.of(event, () -> {
            log.info("Published planning-kpi-updated event: eventId={}, productionLine={}", domainEvent.eventId(),
                    payload.productionLine());

            publicationResult.complete(null);
            return CompletableFuture.completedFuture(null);
        }, failure -> {
            log.error("Failed to publish planning-kpi-updated event: eventId={}, productionLine={}",
                    domainEvent.eventId(), payload.productionLine(), failure);

            publicationResult.completeExceptionally(failure);
            return CompletableFuture.completedFuture(null);
        }).addMetadata(metadata);

        log.info(
                "Publishing planning-kpi-updated event: eventId={}, productionLine={}, currentLoad={}, maximumCapacity={}",
                domainEvent.eventId(), payload.productionLine(), payload.currentLoad(), payload.maximumCapacity());

        planningKpiUpdatedEventEmitter.send(message);
        return publicationResult;
    }
}