package com.smartfactory.order.control.service;

import java.util.ArrayList;
import java.util.List;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.order.boundary.exception_handler.InvalidTransitionException;
import com.smartfactory.order.control.order_service.OrderStatusTransitionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderStatusTransitionHelperTest {

    private List<OrderStatus> globalFailures;

    @BeforeEach
    public void setUp() {
        globalFailures = new ArrayList<>();
        globalFailures.add(OrderStatus.CANCELLED);
    }

    @Test
    public void testLegalTransitionsFromCreated() {
        Assertions.assertDoesNotThrow(() -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.CREATED, OrderStatus.PLANNED);

            for (final OrderStatus failureStatus : globalFailures) {
                OrderStatusTransitionHelper.checkTransition(OrderStatus.CREATED, failureStatus);
            }
        });
    }

    @Test
    public void testLegalTransitionsFromPlanned() {
        Assertions.assertDoesNotThrow(() -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.PLANNED, OrderStatus.IN_PRODUCTION);

            for (final OrderStatus failureStatus : globalFailures) {
                OrderStatusTransitionHelper.checkTransition(OrderStatus.PLANNED, failureStatus);
            }
        });
    }

    @Test
    public void testLegalTransitionsFromInProduction() {
        Assertions.assertDoesNotThrow(() -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.IN_PRODUCTION, OrderStatus.QUALITY_CHECK);

            for (final OrderStatus failureStatus : globalFailures) {
                OrderStatusTransitionHelper.checkTransition(OrderStatus.IN_PRODUCTION, failureStatus);
            }
        });
    }

    @Test
    public void testLegalTransitionsFromQualityCheck() {
        Assertions.assertDoesNotThrow(() -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.QUALITY_CHECK, OrderStatus.COMPLETED);
            OrderStatusTransitionHelper.checkTransition(OrderStatus.QUALITY_CHECK, OrderStatus.REWORK_REQUIRED);
            OrderStatusTransitionHelper.checkTransition(OrderStatus.QUALITY_CHECK, OrderStatus.FAILED);

            for (final OrderStatus failureStatus : globalFailures) {
                OrderStatusTransitionHelper.checkTransition(OrderStatus.QUALITY_CHECK, failureStatus);
            }
        });
    }

    @Test
    public void testLegalTransitionsFromReworkRequired() {
        Assertions.assertDoesNotThrow(() -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.REWORK_REQUIRED, OrderStatus.IN_PRODUCTION);
            OrderStatusTransitionHelper.checkTransition(OrderStatus.REWORK_REQUIRED, OrderStatus.QUALITY_CHECK);

            for (final OrderStatus failureStatus : globalFailures) {
                OrderStatusTransitionHelper.checkTransition(OrderStatus.REWORK_REQUIRED, failureStatus);
            }
        });
    }

    @Test
    public void testIllegalTransitionCreatedToCompleted() {
        Assertions.assertThrows(InvalidTransitionException.class, () -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.CREATED, OrderStatus.COMPLETED);
        });
    }

    @Test
    public void testIllegalTransitionCompletedToPlanned() {
        Assertions.assertThrows(InvalidTransitionException.class, () -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.COMPLETED, OrderStatus.PLANNED);
        });
    }

    @Test
    public void testOtherIllegalTransitions() {
        Assertions.assertThrows(InvalidTransitionException.class, () -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.CREATED, OrderStatus.REWORK_REQUIRED);
        });

        Assertions.assertThrows(InvalidTransitionException.class, () -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.CANCELLED, OrderStatus.IN_PRODUCTION);
        });
    }

    @Test
    public void testNullCurrentStatus() {
        Assertions.assertThrows(InvalidTransitionException.class, () -> {
            OrderStatusTransitionHelper.checkTransition(null, OrderStatus.PLANNED);
        });
    }

    @Test
    public void testNullNextStatus() {
        Assertions.assertThrows(InvalidTransitionException.class, () -> {
            OrderStatusTransitionHelper.checkTransition(OrderStatus.CREATED, null);
        });
    }
}