package com.smartfactory.order.control.order_service;

import static com.smartfactory.common.exception.AssemblyServiceExceptions.VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.OrderErrorCodes.ORDER_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;

import com.smartfactory.common.dto.assembly.VehicleProductionView;
import com.smartfactory.common.dto.order.OrderProgressView;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import com.smartfactory.order.control.mapper.OrderCompletedPayloadMapper;
import com.smartfactory.order.control.mapper.OrderStatusUpdatedPayloadMapper;
import com.smartfactory.order.control.publisher.OrderEventPublisher;
import com.smartfactory.order.control.repository.OrderRepository;
import com.smartfactory.order.entity.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrderProgressService {
    private static final Logger log = LoggerFactory.getLogger(OrderProgressService.class);
    @Inject
    OrderRepository repo;
    @Inject
    OrderEventPublisher publisher;
    @Inject
    OrderCompletedPayloadMapper payloadMapper;
    @Inject
    @RestClient
    AssemblyClient assemblyClient;

    public OrderProgressView getProgressView(final String orderId) {

        final Order order = repo.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException("Order not found: " + orderId, ORDER_NOT_FOUND, 404));

        return new OrderProgressView(order.getOrderId(), order.getVehicleId(), order.getStatus(),
                resolveCurrentFactoryStage(order), order.getCompletionDate(), order.getLastModifiedDate());
    }

    private ProductionStage resolveCurrentFactoryStage(final Order order) {

        try {
            final VehicleProductionView vehicleProduction = assemblyClient.findByVehicleId(order.getVehicleId());

            return vehicleProduction.currentStage();
        } catch (final WebApplicationException e) {

            if (e.getResponse() != null && e.getResponse().getStatus() == 404) {
                throw new BusinessException("Vehicle production not found for vehicle: " + order.getVehicleId(),
                        VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, 404);
            }

            throw e;
        }
    }

    @Transactional
    public void completeOrderFromQualityApproval(final String eventId, final QualityApprovedPayload payload) {
        try {

            log.info("Processing quality-approved event for vehicle {}", payload.vehicleId());

            // Idempotency check
            if (repo.isEventProcessed(eventId)) {
                log.debug("Event {} already processed", eventId);
                return;
            }

            final var order = repo.findByVehicleId(payload.vehicleId()).orElseThrow(
                    () -> new BusinessException("Order not found for vehicle " + payload.vehicleId(), "ORDER_NOT_FOUND",
                            404));

            // Validate status transition
            OrderStatusTransitionHelper.checkTransition(order.getStatus(), OrderStatus.COMPLETED);

            order.setStatus(OrderStatus.COMPLETED);
            order.setLastModifiedDate(LocalDateTime.now());
            order.setCompletionDate(LocalDateTime.now());
            repo.update(order);

            // Mark event as processed
            repo.markEventProcessed(eventId);

            // Emit order-completed event
            publisher.publishOrderCompleted(payloadMapper.toPayload(order));
            log.info("Order {} completed for vehicle {}", order.getOrderId(), payload.vehicleId());
        } catch (final BusinessException e) {
            log.error("Business error: {}", e.getErrorCode(), e);
            throw e;
        } catch (final Exception e) {
            log.error("Unexpected error processing quality-approved", e);
            throw new BusinessException("Failed to complete order from quality approval", "ORDER_COMPLETION_ERROR", e);
        }
    }

    @Transactional
    public void markOrderForRework(final @NotBlank String eventId, final QualityReworkRequiredPayload payload) {
        if (repo.isEventProcessed(eventId)) {
            return;
        }

        final var order = repo.findByVehicleId(payload.vehicleId()).orElseThrow(
                () -> new BusinessException("Order not found for vehicle " + payload.vehicleId(), "ORDER_NOT_FOUND",
                        404));

        OrderStatusTransitionHelper.checkTransition(order.getStatus(), OrderStatus.REWORK_REQUIRED);

        final var oldStatus = order.getStatus();
        order.setLastModifiedDate(LocalDateTime.now());
        order.setStatus(OrderStatus.REWORK_REQUIRED);
        repo.update(order);
        repo.markEventProcessed(eventId);

        publisher.publishOrderStatusUpdated(OrderStatusUpdatedPayloadMapper.build(order, oldStatus));

    }

    /**
     * Moves the order into {@link OrderStatus#QUALITY_CHECK} when the vehicle leaves the assembly line.
     *
     * <p>The order lifecycle is CREATED -> PLANNED -> IN_PRODUCTION -> QUALITY_CHECK. Order service does not
     * consume the intermediate planning/inventory events, so without this step the order would still be in
     * CREATED when the quality decision arrives and every quality transition would be rejected. The order is
     * therefore fast-forwarded through the intermediate states here, emitting one status event per hop so the
     * DWH timeline stays complete.
     */
    @Transactional
    public void markOrderForQualityCheck(final String eventId, final VehicleAssembledPayload payload) {
        if (repo.isEventProcessed(eventId)) {
            return;
        }

        final var order = repo.findByVehicleId(payload.vehicleId()).orElseThrow(
                () -> new BusinessException("Order not found for vehicle " + payload.vehicleId(), "ORDER_NOT_FOUND",
                        404));

        repo.markEventProcessed(eventId);

        if (order.getStatus() == OrderStatus.QUALITY_CHECK) {
            log.debug("Order {} is already in QUALITY_CHECK", order.getOrderId());
            return;
        }

        for (final OrderStatus next : pathToQualityCheck(order.getStatus())) {
            final var oldStatus = order.getStatus();

            OrderStatusTransitionHelper.checkTransition(oldStatus, next);

            order.setStatus(next);
            order.setLastModifiedDate(LocalDateTime.now());
            repo.update(order);

            publisher.publishOrderStatusUpdated(OrderStatusUpdatedPayloadMapper.build(order, oldStatus));
        }

        log.info("Order {} moved to QUALITY_CHECK for vehicle {}", order.getOrderId(), payload.vehicleId());
    }

    /**
     * Returns the statuses the order still has to walk through to reach QUALITY_CHECK.
     * A reworked vehicle sits in REWORK_REQUIRED and can move to QUALITY_CHECK directly.
     */
    private List<OrderStatus> pathToQualityCheck(final OrderStatus current) {
        return switch (current) {
            case CREATED -> List.of(OrderStatus.PLANNED, OrderStatus.IN_PRODUCTION, OrderStatus.QUALITY_CHECK);
            case PLANNED -> List.of(OrderStatus.IN_PRODUCTION, OrderStatus.QUALITY_CHECK);
            case IN_PRODUCTION, REWORK_REQUIRED -> List.of(OrderStatus.QUALITY_CHECK);
            default -> List.of();
        };
    }

    /**
     * Marks the order as FAILED when quality scraps the vehicle (inspection score below the rework threshold,
     * or the rework budget exhausted). FAILED is a terminal state, so no further transition is possible.
     */
    @Transactional
    public void failOrderFromQualityFailure(final String eventId, final QualityFailedPayload payload) {
        if (repo.isEventProcessed(eventId)) {
            log.debug("Event {} already processed", eventId);
            return;
        }

        final var order = repo.findByVehicleId(payload.vehicleId()).orElseThrow(
                () -> new BusinessException("Order not found for vehicle " + payload.vehicleId(), "ORDER_NOT_FOUND",
                        404));

        OrderStatusTransitionHelper.checkTransition(order.getStatus(), OrderStatus.FAILED);

        final var oldStatus = order.getStatus();
        order.setStatus(OrderStatus.FAILED);
        order.setLastModifiedDate(LocalDateTime.now());
        repo.update(order);
        repo.markEventProcessed(eventId);

        publisher.publishOrderStatusUpdated(OrderStatusUpdatedPayloadMapper.build(order, oldStatus));

        log.info("Order {} marked FAILED for vehicle {} (score={}, defects={})", order.getOrderId(),
                payload.vehicleId(), payload.score(), payload.defectCodes());
    }

}



