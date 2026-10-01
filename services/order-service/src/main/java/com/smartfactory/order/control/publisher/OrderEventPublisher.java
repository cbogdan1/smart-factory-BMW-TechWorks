package com.smartfactory.order.control.publisher;

import java.util.UUID;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.OrderCompletedEvent;
import com.smartfactory.common.event.OrderCreatedEvent;
import com.smartfactory.common.event.OrderStatusUpdatedEvent;
import com.smartfactory.common.payloads.order_service.OrderCompletedPayload;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.common.payloads.order_service.OrderStatusUpdatedPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrderEventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(OrderEventPublisher.class);

    @Inject
    @Channel("order-created")
    Emitter<OrderCreatedEvent> orderCreatedEmitter;

    @Inject
    @Channel("order-status-updated")
    Emitter<OrderStatusUpdatedEvent> updatedStatusEmitter;

    @Inject
    @Channel("order-completed")
    Emitter<OrderCompletedEvent> orderCompletedEmitter;

    public void publishOrderCreated(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) final OrderCreatedPayload payload) {
        final String eventId = "EVT-" + UUID.randomUUID();
        final String vehicleId = payload.vehicleId(); // key & correlationId

        final DomainEvent<OrderCreatedPayload> domainEvent =
                new DomainEvent<>(eventId, Topics.ORDER_CREATED, "order-service", vehicleId, payload);
        final OrderCreatedEvent kafkaMessage = new OrderCreatedEvent(domainEvent);

        orderCreatedEmitter.send(kafkaMessage).whenComplete((res, ex) -> {
            if (ex != null) {
                LOG.error("Failed to publish {} for vehicleId: {} with message : {}", Topics.ORDER_CREATED, vehicleId,
                        ex.getMessage());
            } else {
                LOG.info("Successfully published {} [eventId={}, vehicleId={}]", Topics.ORDER_CREATED, eventId,
                        vehicleId);
            }
        });
    }

    public void publishOrderStatusUpdated(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) final OrderStatusUpdatedPayload payload) {
        final String eventId = "EVT-" + UUID.randomUUID();
        final String vehicleId = payload.vehicleId();

        final DomainEvent<OrderStatusUpdatedPayload> domainEvent =
                new DomainEvent<>(eventId, Topics.ORDER_STATUS_UPDATED, "order-service", vehicleId, payload);
        final OrderStatusUpdatedEvent kafkaMessage = new OrderStatusUpdatedEvent(domainEvent);

        updatedStatusEmitter.send(kafkaMessage).whenCompleteAsync((res, ex) -> {
            if (ex != null) {
                LOG.error("Failed to publish {} for vehicleId: {} with message : {}", Topics.ORDER_STATUS_UPDATED,
                        vehicleId, ex.getMessage());
            } else {
                LOG.info("Successfully published {} [eventId={}, vehicleId={}]", Topics.ORDER_STATUS_UPDATED, eventId,
                        vehicleId);
            }
        });
    }

    public void publishOrderCompleted(final OrderCompletedPayload payload) {
        final String eventId = "EVT-" + UUID.randomUUID();
        final String vehicleId = payload.vehicleId();

        final DomainEvent<OrderCompletedPayload> domainEvent =
                new DomainEvent<>(eventId, Topics.ORDER_COMPLETED, "order-service", vehicleId, payload);
        final OrderCompletedEvent kafkaMessage = new OrderCompletedEvent(domainEvent);

        //use whenCompleteAsync because we want to run logging in separate thread, and not in the kafka thread coupled with the @Transaction-method thread so we dont have 2 threads related to the transaction(exception)
        orderCompletedEmitter.send(kafkaMessage).whenCompleteAsync((res, ex) -> {
            if (ex != null) {
                LOG.error("Failed to publish {} for vehicleId: {} with message : {}", Topics.ORDER_COMPLETED, vehicleId,
                        ex.getMessage());
            } else {
                LOG.info("Successfully published {} [eventId={}, vehicleId={}]", Topics.ORDER_COMPLETED, eventId,
                        vehicleId);
            }
        });

    }
}
