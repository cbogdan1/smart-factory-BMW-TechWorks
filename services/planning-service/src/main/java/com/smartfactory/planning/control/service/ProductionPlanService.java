package com.smartfactory.planning.control.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.smartfactory.common.Topics;
import com.smartfactory.common.dto.planning.CreateProductionPlanRequest;
import com.smartfactory.common.dto.planning.ProductionPlanResponse;
import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionPlanStatus;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.PlanningServiceExceptions;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;
import com.smartfactory.common.payloads.planning_service.PlanningKpiUpdatedPayload;
import com.smartfactory.planning.boundary.kafka.PlanningEventProducer;
import com.smartfactory.planning.control.repository.ProductionLineRepository;
import com.smartfactory.planning.control.repository.ProductionPlanRepository;
import com.smartfactory.planning.entity.ProductionLine;
import com.smartfactory.planning.entity.ProductionPlan;
import com.smartfactory.planning.utility.ProductionPlanMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ProductionPlanService {
    @Inject
    EntityManager entityManager;

    @Inject
    ProductionPlanRepository productionPlanRepository;

    @Inject
    PlanningEventProducer planningEventProducer;

    @Inject
    ProductionLineRepository productionLineRepository;

    @Transactional
    public Optional<ProductionPlan> createPlan(final String eventId, final String eventType, final String orderId,
            final String vehicleId, final String vehicleModel, final Priority priority) {

        final int insertedRows = entityManager.createNativeQuery("""
                INSERT INTO processed_event (
                    event_id,
                    event_type,
                    processed_at
                )
                VALUES (
                    :eventId,
                    :eventType,
                    CURRENT_TIMESTAMP
                )
                ON CONFLICT DO NOTHING
                """).setParameter("eventId", eventId).setParameter("eventType", eventType).executeUpdate();

        if (insertedRows == 0) {
            log.info("Event already processed: eventId={}", eventId);
            return Optional.empty();
        }

        final ProductionPlan productionPlan = new ProductionPlan();

        productionPlan.setOrderId(orderId);
        productionPlan.setVehicleId(vehicleId);
        productionPlan.setVehicleModel(vehicleModel);
        productionPlan.setProductionLine(assignLine());
        productionPlan.setPriority(priority);
        productionPlan.setPlannedStartDate(LocalDate.now());
        productionPlan.setCreatedDate(LocalDateTime.now());
        productionPlan.setStatus(ProductionPlanStatus.PLANNED);
        productionPlan.setCurrentStage(ProductionStage.CREATED);

        productionPlanRepository.persist(productionPlan);
        productionPlanRepository.flush();

        planningEventProducer.publishProductionPlanned(ProductionPlanMapper.toPayload(productionPlan))
                .toCompletableFuture().join();

        return Optional.of(productionPlan);
    }

    @Transactional
    public ProductionPlanResponse create(final CreateProductionPlanRequest request) {
        final ProductionPlan productionPlan = new ProductionPlan();

        productionPlan.setOrderId(request.getOrderId());
        productionPlan.setVehicleId(request.getVehicleId());
        productionPlan.setVehicleModel(request.getVehicleModel());
        productionPlan.setProductionLine(assignLine());
        productionPlan.setPriority(request.getPriority());
        productionPlan.setPlannedStartDate(request.getPlannedStartDate());
        productionPlan.setCreatedDate(LocalDateTime.now());
        productionPlan.setStatus(ProductionPlanStatus.PLANNED);
        productionPlan.setCurrentStage(ProductionStage.CREATED);

        productionPlanRepository.persist(productionPlan);
        productionPlanRepository.flush();

        planningEventProducer.publishProductionPlanned(ProductionPlanMapper.toPayload(productionPlan))
                .toCompletableFuture().join();

        return ProductionPlanMapper.toResponse(productionPlan);
    }

    public ProductionPlanResponse getById(final String planId) {
        final ProductionPlan productionPlan = productionPlanRepository.findByIdOptional(planId).orElseThrow(
                () -> new BusinessException("Production plan not found: " + planId,
                        PlanningServiceExceptions.PLAN_NOT_FOUND, 404));

        return ProductionPlanMapper.toResponse(productionPlan);
    }

    public List<ProductionPlanResponse> getAll() {
        return productionPlanRepository.listAll().stream().map(ProductionPlanMapper::toResponse).toList();
    }

    private String assignLine() {
        final ProductionLine productionLine = productionLineRepository.findLeastLoadedLineForUpdate().orElseThrow(
                () -> new BusinessException("No enabled production line available",
                        PlanningServiceExceptions.NO_PRODUCTION_LINE_AVAILABLE, 503));

        if (productionLine.getCurrentLoad() >= productionLine.getMaximumCapacity()) {
            throw new BusinessException(
                    String.format("Production line capacity exceeded for %s (%d/%d)", productionLine.getLineId(),
                            productionLine.getCurrentLoad(), productionLine.getMaximumCapacity()),
                    PlanningServiceExceptions.PRODUCTION_LINE_CAPACITY_EXCEEDED, 409);
        }

        productionLineRepository.incrementLoad(productionLine);

        log.info("Production line assigned: lineId={}, currentLoad={}, maximumCapacity={}", productionLine.getLineId(),
                productionLine.getCurrentLoad(), productionLine.getMaximumCapacity());

        return productionLine.getLineId();
    }

    @Transactional
    public void advanceVehicleStage(final String eventId, final VehicleStageAdvancedPayload payload) {
        final int insertedRows = entityManager.createNativeQuery("""
                        INSERT INTO processed_event (
                            event_id,
                            event_type,
                            processed_at
                        )
                        VALUES (
                            :eventId,
                            :eventType,
                            CURRENT_TIMESTAMP
                        )
                        ON CONFLICT DO NOTHING
                        """).setParameter("eventId", eventId).setParameter("eventType", Topics.VEHICLE_STAGE_ADVANCED)
                .executeUpdate();

        if (insertedRows == 0) {
            log.info("Event already processed: eventId={}", eventId);
            return;
        }

        final ProductionPlan productionPlan = productionPlanRepository.findByVehicleId(payload.vehicleId()).orElseThrow(
                () -> new BusinessException("Production plan not found for vehicle " + payload.vehicleId(),
                        PlanningServiceExceptions.PLAN_NOT_FOUND, 404));

        if (!isValidStageTransition(payload.previousStage(), payload.currentStage())) {
            throw new BusinessException(
                    "Invalid stage transition: " + payload.previousStage() + " -> " + payload.currentStage(),
                    PlanningServiceExceptions.INVALID_STAGE_TRANSITION, 409);
        }

        /*
         * During a rework loop Assembly rewinds the vehicle to an earlier stage (e.g. ASSEMBLED -> PAINT)
         * without emitting a stage-advanced event for the rewind itself, so the plan legitimately runs
         * AHEAD of the incoming event. Re-synchronise in that case, otherwise every rework would push the
         * remaining stage events into the DLQ.
         *
         * The plan running BEHIND the event is a different story: it means a vehicle-stage-advanced event
         * was lost or arrived out of order. That must still fail loudly instead of being silently skipped.
         */
        if (productionPlan.getCurrentStage().ordinal() < payload.previousStage().ordinal()) {
            throw new BusinessException(
                    "Production plan is at stage " + productionPlan.getCurrentStage() + " but event expects stage "
                            + payload.previousStage(), PlanningServiceExceptions.INVALID_STAGE_TRANSITION, 409);
        }

        if (productionPlan.getCurrentStage() != payload.previousStage()) {
            log.info("Re-syncing production plan after rework: vehicleId={}, planStage={}, eventPreviousStage={}",
                    payload.vehicleId(), productionPlan.getCurrentStage(), payload.previousStage());
        }

        productionPlan.setCurrentStage(payload.currentStage());

        log.info("Production plan stage advanced: vehicleId={}, from={}, to={}", payload.vehicleId(),
                payload.previousStage(), payload.currentStage());
    }

    @Transactional
    public void completePlan(final String eventId, final VehicleAssembledPayload payload) {
        final int insertedRows = entityManager.createNativeQuery("""
                        INSERT INTO processed_event (
                            event_id,
                            event_type,
                            processed_at
                        )
                        VALUES (
                            :eventId,
                            :eventType,
                            CURRENT_TIMESTAMP
                        )
                        ON CONFLICT DO NOTHING
                        """).setParameter("eventId", eventId).setParameter("eventType", Topics.VEHICLE_ASSEMBLED)
                .executeUpdate();

        if (insertedRows == 0) {
            log.info("Event already processed: eventId={}", eventId);
            return;
        }

        final ProductionPlan productionPlan = productionPlanRepository.findByVehicleId(payload.vehicleId()).orElseThrow(
                () -> new BusinessException("Production plan not found for vehicle " + payload.vehicleId(),
                        PlanningServiceExceptions.PLAN_NOT_FOUND, 404));

        if (productionPlan.getStatus() == ProductionPlanStatus.COMPLETED) {
            log.info("Production plan already completed: vehicleId = {}", payload.vehicleId());
            return;
        }

        productionPlan.setStatus(ProductionPlanStatus.COMPLETED);
        productionPlan.setCompletedAt(LocalDateTime.now());

        /*
         * The production plan already contains the ID of the
         * production line assigned to this vehicle.
         *
         * We retrieve that line with a pessimistic lock and
         * decrement its current load.
         */
        final ProductionLine productionLine =
                productionLineRepository.findByLineIdForUpdate(productionPlan.getProductionLine()).orElseThrow(
                        () -> new BusinessException("Production line not found: " + productionPlan.getProductionLine(),
                                PlanningServiceExceptions.PRODUCTION_LINE_NOT_FOUND, 404));

        productionLineRepository.decrementLoad(productionLine);

        /*
         * Publish the updated production-line KPI after
         * the load has been decremented.
         */
        final PlanningKpiUpdatedPayload kpiPayload =
                new PlanningKpiUpdatedPayload(productionLine.getLineId(), productionLine.getCurrentLoad(),
                        productionLine.getMaximumCapacity());

        planningEventProducer.publishPlanningKpiUpdated(kpiPayload).toCompletableFuture().join();

        log.info("Production plan completed: vehicleId={}, planId={}, productionLine={}, currentLoad={}",
                payload.vehicleId(), productionPlan.getPlanId(), productionLine.getLineId(),
                productionLine.getCurrentLoad());
    }

    private boolean isValidStageTransition(final ProductionStage from, final ProductionStage to) {
        final ProductionStage[] sequence =
                { ProductionStage.CREATED, ProductionStage.BODY, ProductionStage.PAINT, ProductionStage.INTERIOR,
                        ProductionStage.POWERTRAIN, ProductionStage.FINAL, ProductionStage.ASSEMBLED };

        final int fromIndex = Arrays.asList(sequence).indexOf(from);
        final int toIndex = Arrays.asList(sequence).indexOf(to);

        return fromIndex >= 0 && toIndex == fromIndex + 1;
    }
}