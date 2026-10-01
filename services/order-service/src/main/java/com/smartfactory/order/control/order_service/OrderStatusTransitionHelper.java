package com.smartfactory.order.control.order_service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.order.boundary.exception_handler.InvalidTransitionException;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderStatusTransitionHelper {
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    private static final Set<OrderStatus> GLOBAL_FAILURES = Set.of(OrderStatus.CANCELLED);

    static {

        //CREATED -> PLANNED
        ALLOWED_TRANSITIONS.put(OrderStatus.CREATED, combineWithGlobalTransitions(Set.of(OrderStatus.PLANNED)));

        // PLANNED -> IN_PRODUCTION
        ALLOWED_TRANSITIONS.put(OrderStatus.PLANNED, combineWithGlobalTransitions(Set.of(OrderStatus.IN_PRODUCTION)));

        // IN_PRODUCTION -> QUALITY_CHECK
        ALLOWED_TRANSITIONS.put(OrderStatus.IN_PRODUCTION,
                combineWithGlobalTransitions(Set.of(OrderStatus.QUALITY_CHECK)));

        // QUALITY_CHECK -> COMPLETED / REWORK_REQUIRED / FAILED
        ALLOWED_TRANSITIONS.put(OrderStatus.QUALITY_CHECK, combineWithGlobalTransitions(
                Set.of(OrderStatus.COMPLETED, OrderStatus.REWORK_REQUIRED, OrderStatus.FAILED)));

        // REWORK_REQUIRED -> IN_PRODUCTION / QUALITY_CHECK / COMPLETED / FAILED
        // FAILED is required for the rework->scrap loop: a vehicle that is sent back for rework and
        // then fails the follow-up inspection (or exhausts its rework budget) must still be able to
        // reach a terminal FAILED state.
        ALLOWED_TRANSITIONS.put(OrderStatus.REWORK_REQUIRED, combineWithGlobalTransitions(
                Set.of(OrderStatus.IN_PRODUCTION, OrderStatus.QUALITY_CHECK, OrderStatus.COMPLETED,
                        OrderStatus.FAILED)));

        ALLOWED_TRANSITIONS.put(OrderStatus.COMPLETED, Set.of());
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of());
        ALLOWED_TRANSITIONS.put(OrderStatus.FAILED, Set.of());

    }

    private static Set<OrderStatus> combineWithGlobalTransitions(final Set<OrderStatus> specificTransition) {
        final Set<OrderStatus> combinedTransitions = EnumSet.copyOf(specificTransition);
        combinedTransitions.addAll(GLOBAL_FAILURES);
        return combinedTransitions;
    }

    public static void checkTransition(final OrderStatus current, final OrderStatus next) {
        if (current == null || next == null) {
            throw new InvalidTransitionException("Null transition: current " + current + "next :" + next);
        }
        if (!ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(next)) {
            throw new InvalidTransitionException(
                    "Invalid transition, check factory pipeline: current [" + current + "] next [" + next + "]");
        }
    }

}