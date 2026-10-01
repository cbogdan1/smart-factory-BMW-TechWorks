package com.smartfactory.planning.boundary.kafka;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.smartfactory.common.Topics;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.OrderCreatedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.PlanningServiceExceptions;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.planning.control.service.ProductionPlanService;
import com.smartfactory.planning.entity.ProductionPlan;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
public class OrderEventConsumer {
    @Inject
    Validator validator;

    @Inject
    ProductionPlanService productionPlanService;

    @Incoming(Topics.ORDER_CREATED)
    @Blocking
    public void consume(final OrderCreatedEvent orderCreatedEvent) {
        if (orderCreatedEvent == null || orderCreatedEvent.event() == null) {
            throw new BusinessException("Order-created event must not be null",
                    PlanningServiceExceptions.INVALID_ORDER_CREATED_EVENT, 400);
        }

        final DomainEvent<OrderCreatedPayload> event = orderCreatedEvent.event();

        validate(event);
        validateEventType(event);

        final Optional<ProductionPlan> result =
                productionPlanService.createPlan(event.eventId(), event.eventType(), event.payload().orderId(),
                        event.payload().vehicleId(), event.payload().vehicleModel(), event.payload().priority());

        if (result.isEmpty()) {
            log.info("Ignoring duplicate event: eventId={}", event.eventId());
            return;
        }

        log.info("Created production plan for orderId={}", event.payload().orderId());
    }

    private void validate(final DomainEvent<OrderCreatedPayload> event) {
        final Set<ConstraintViolation<DomainEvent<OrderCreatedPayload>>> violations = validator.validate(event);

        if (violations.isEmpty()) {
            return;
        }

        final String details = violations.stream().map(v -> v.getPropertyPath() + " " + v.getMessage()).sorted()
                .collect(Collectors.joining(", "));

        throw new BusinessException("Invalid order-created event: " + details,
                PlanningServiceExceptions.INVALID_ORDER_CREATED_EVENT, 400);
    }

    private void validateEventType(final DomainEvent<OrderCreatedPayload> event) {
        if (!Topics.ORDER_CREATED.equals(event.eventType())) {
            throw new BusinessException(
                    "Expected eventType " + Topics.ORDER_CREATED + " but received " + event.eventType(),
                    PlanningServiceExceptions.INVALID_EVENT_TYPE, 400);
        }
    }
}