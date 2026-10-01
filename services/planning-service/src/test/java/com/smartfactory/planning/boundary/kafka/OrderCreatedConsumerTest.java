package com.smartfactory.planning.boundary.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import com.smartfactory.common.Topics;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionPlanStatus;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.OrderCreatedEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.PlanningServiceExceptions;
import com.smartfactory.common.payloads.order_service.OrderCreatedPayload;
import com.smartfactory.planning.control.service.ProductionPlanService;
import com.smartfactory.planning.entity.ProductionPlan;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class OrderCreatedConsumerTest {
    @Inject
    OrderEventConsumer consumer;

    @InjectMock
    ProductionPlanService productionPlanService;

    @Test
    void shouldDelegateValidEventToPlanningService() {
        when(productionPlanService.createPlan("EVT-000001", Topics.ORDER_CREATED, "ORD-000001", "VEH-000001", "i4",
                Priority.HIGH)).thenReturn(Optional.of(plan()));

        consumer.consume(validEvent("EVT-000001"));

        verify(productionPlanService).createPlan("EVT-000001", Topics.ORDER_CREATED, "ORD-000001", "VEH-000001", "i4",
                Priority.HIGH);
    }

    @Test
    void shouldHandleDuplicateEventWithoutFailure() {
        when(productionPlanService.createPlan("EVT-000001", Topics.ORDER_CREATED, "ORD-000001", "VEH-000001", "i4",
                Priority.HIGH)).thenReturn(Optional.empty());

        consumer.consume(validEvent("EVT-000001"));

        verify(productionPlanService).createPlan("EVT-000001", Topics.ORDER_CREATED, "ORD-000001", "VEH-000001", "i4",
                Priority.HIGH);
    }

    @Test
    void shouldRejectMissingEventId() {
        final OrderCreatedEvent event = new OrderCreatedEvent(
                new DomainEvent<>(null, Topics.ORDER_CREATED, "1.0", Instant.parse("2026-08-01T10:15:00Z"),
                        "order-service", "VEH-000001", validPayload()));

        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume(event));

        assertTrue(exception.getMessage().contains("eventId"));
        assertEquals(PlanningServiceExceptions.INVALID_ORDER_CREATED_EVENT, exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(productionPlanService);
    }

    @Test
    void shouldRejectPayloadWithMissingVehicleId() {
        final OrderCreatedPayload payload =
                new OrderCreatedPayload("ORD-000001", null, "My Name", "john@email", "i4", "Blue", "LONG-RANGE",
                        Priority.HIGH, OrderStatus.CREATED);

        final OrderCreatedEvent event = new OrderCreatedEvent(
                new DomainEvent<>("EVT-000002", Topics.ORDER_CREATED, "1.0", Instant.parse("2026-08-01T10:15:00Z"),
                        "order-service", "VEH-000001", payload));

        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume(event));

        assertTrue(exception.getMessage().contains("vehicleId"));
        assertEquals(PlanningServiceExceptions.INVALID_ORDER_CREATED_EVENT, exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(productionPlanService);
    }

    @Test
    void shouldRejectNullPayload() {
        final OrderCreatedEvent event = new OrderCreatedEvent(
                new DomainEvent<>("EVT-000010", Topics.ORDER_CREATED, "1.0", Instant.now(), "order-service",
                        "VEH-000001", null));

        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume(event));

        assertEquals(PlanningServiceExceptions.INVALID_ORDER_CREATED_EVENT, exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(productionPlanService);
    }

    @Test
    void shouldRejectWrongEventType() {
        final OrderCreatedEvent event = new OrderCreatedEvent(
                new DomainEvent<>("EVT-000003", Topics.PARTS_RESERVED, "1.0", Instant.parse("2026-08-01T10:15:00Z"),
                        "order-service", "VEH-000001", validPayload()));

        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume(event));

        assertTrue(exception.getMessage().contains(Topics.ORDER_CREATED));
        assertEquals(PlanningServiceExceptions.INVALID_EVENT_TYPE, exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(productionPlanService);
    }

    @Test
    void shouldRejectNullEvent() {
        final BusinessException exception = assertThrows(BusinessException.class, () -> consumer.consume(null));

        assertEquals(PlanningServiceExceptions.INVALID_ORDER_CREATED_EVENT, exception.getErrorCode());
        assertEquals(400, exception.getStatusCode());

        verifyNoInteractions(productionPlanService);
    }

    @Test
    void shouldPropagateProductionLineCapacityException() {
        when(productionPlanService.createPlan("EVT-000001", Topics.ORDER_CREATED, "ORD-000001", "VEH-000001", "i4",
                Priority.HIGH)).thenThrow(new BusinessException("Production line capacity exceeded",
                PlanningServiceExceptions.PRODUCTION_LINE_CAPACITY_EXCEEDED, 409));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> consumer.consume(validEvent("EVT-000001")));

        assertEquals(PlanningServiceExceptions.PRODUCTION_LINE_CAPACITY_EXCEEDED, exception.getErrorCode());
        assertEquals("Production line capacity exceeded", exception.getMessage());
        assertEquals(409, exception.getStatusCode());

        verify(productionPlanService).createPlan("EVT-000001", Topics.ORDER_CREATED, "ORD-000001", "VEH-000001", "i4",
                Priority.HIGH);
    }

    private OrderCreatedEvent validEvent(final String eventId) {
        return new OrderCreatedEvent(
                new DomainEvent<>(eventId, Topics.ORDER_CREATED, "1.0", Instant.parse("2026-08-01T10:15:00Z"),
                        "order-service", "VEH-000001", validPayload()));
    }

    private OrderCreatedPayload validPayload() {
        return new OrderCreatedPayload("ORD-000001", "VEH-000001", "My Name", "john@email", "i4", "Blue", "LONG-RANGE",
                Priority.HIGH, OrderStatus.CREATED);
    }

    private ProductionPlan plan() {
        final ProductionPlan plan = new ProductionPlan();

        plan.setPlanId("PLAN-000001");
        plan.setOrderId("ORD-000001");
        plan.setVehicleId("VEH-000001");
        plan.setVehicleModel("i4");
        plan.setProductionLine("LINE-1");
        plan.setPriority(Priority.HIGH);
        plan.setPlannedStartDate(LocalDate.of(2026, 8, 10));
        plan.setStatus(ProductionPlanStatus.PLANNED);

        return plan;
    }
}