package com.smartfactory.planning.control.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import com.smartfactory.common.payloads.planning_service.ProductionPlannedPayload;
import com.smartfactory.planning.boundary.kafka.PlanningEventProducer;
import com.smartfactory.planning.control.repository.ProductionLineRepository;
import com.smartfactory.planning.control.repository.ProductionPlanRepository;
import com.smartfactory.planning.entity.ProductionLine;
import com.smartfactory.planning.entity.ProductionPlan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductionPlanServiceTest {
    @Mock
    ProductionPlanRepository productionPlanRepository;

    @Mock
    ProductionLineRepository productionLineRepository;

    @Mock
    EntityManager entityManager;

    @Mock
    Query query;

    @Mock
    PlanningEventProducer planningEventProducer;

    @InjectMocks
    ProductionPlanService productionPlanService;

    private ProductionPlan productionPlan;
    private ProductionLine productionLine;

    @BeforeEach
    void setUp() {
        productionPlan = new ProductionPlan();

        productionPlan.setPlanId("PLAN-001");
        productionPlan.setOrderId("ORDER-001");
        productionPlan.setVehicleId("VEHICLE-001");
        productionPlan.setVehicleModel("BMW i4");
        productionPlan.setProductionLine("LINE-1");
        productionPlan.setPriority(Priority.HIGH);
        productionPlan.setPlannedStartDate(LocalDate.of(2026, 8, 10));
        productionPlan.setStatus(ProductionPlanStatus.PLANNED);
        productionPlan.setCurrentStage(ProductionStage.CREATED);
        productionPlan.setCreatedDate(LocalDateTime.now());

        productionLine = new ProductionLine();
        productionLine.setLineId("LINE-1");
        productionLine.setName("Assembly Line 1");
        productionLine.setMaximumCapacity(5);
        productionLine.setCurrentLoad(0);
        productionLine.setEnabled(true);
    }

    @Test
    void shouldCreateProductionPlanFromRequest() {
        when(planningEventProducer.publishProductionPlanned(any(ProductionPlannedPayload.class))).thenReturn(
                CompletableFuture.completedFuture(null));

        when(productionLineRepository.findLeastLoadedLineForUpdate()).thenReturn(Optional.of(productionLine));

        final CreateProductionPlanRequest request = new CreateProductionPlanRequest();

        request.setOrderId("ORDER-001");
        request.setVehicleId("VEHICLE-001");
        request.setVehicleModel("BMW i4");
        request.setPriority(Priority.HIGH);
        request.setPlannedStartDate(LocalDate.of(2026, 8, 10));

        final ProductionPlanResponse response = productionPlanService.create(request);

        assertNotNull(response);
        assertEquals("ORDER-001", response.getOrderId());
        assertEquals("VEHICLE-001", response.getVehicleId());
        assertEquals("BMW i4", response.getVehicleModel());
        assertEquals("LINE-1", response.getProductionLine());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(LocalDate.of(2026, 8, 10), response.getPlannedStartDate());
        assertEquals(ProductionPlanStatus.PLANNED, response.getStatus());
        assertEquals(ProductionStage.CREATED, response.getCurrentStage());

        verify(productionPlanRepository).persist(any(ProductionPlan.class));
        verify(productionPlanRepository).flush();
        verify(productionLineRepository).incrementLoad(productionLine);
        verify(planningEventProducer).publishProductionPlanned(any(ProductionPlannedPayload.class));
    }

    @Test
    void shouldCreatePlanFromKafkaEvent() {
        when(planningEventProducer.publishProductionPlanned(any(ProductionPlannedPayload.class))).thenReturn(
                CompletableFuture.completedFuture(null));
        when(productionLineRepository.findLeastLoadedLineForUpdate()).thenReturn(Optional.of(productionLine));
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        final Optional<ProductionPlan> result =
                productionPlanService.createPlan("EVT-001", "order-created", "ORDER-001", "VEHICLE-001", "BMW i4",
                        Priority.HIGH);

        assertTrue(result.isPresent());
        assertEquals("LINE-1", result.get().getProductionLine());
        assertEquals(ProductionPlanStatus.PLANNED, result.get().getStatus());
        assertEquals(ProductionStage.CREATED, result.get().getCurrentStage());

        verify(query).setParameter("eventId", "EVT-001");
        verify(query).setParameter("eventType", "order-created");
        verify(productionPlanRepository).persist(any(ProductionPlan.class));
        verify(productionPlanRepository).flush();
        verify(productionLineRepository).incrementLoad(productionLine);
        verify(planningEventProducer).publishProductionPlanned(any(ProductionPlannedPayload.class));
    }

    @Test
    void shouldIgnoreDuplicateEvent() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        final Optional<ProductionPlan> result =
                productionPlanService.createPlan("EVT-001", "order-created", "ORDER-001", "VEHICLE-001", "BMW i4",
                        Priority.HIGH);

        assertTrue(result.isEmpty());

        verify(productionPlanRepository, never()).persist(any(ProductionPlan.class));
        verify(productionPlanRepository, never()).flush();
        verify(planningEventProducer, never()).publishProductionPlanned(any(ProductionPlannedPayload.class));
        verify(productionLineRepository, never()).findLeastLoadedLineForUpdate();
    }

    @Test
    void shouldReturnProductionPlanById() {
        when(productionPlanRepository.findByIdOptional("PLAN-001")).thenReturn(Optional.of(productionPlan));

        final ProductionPlanResponse response = productionPlanService.getById("PLAN-001");

        assertEquals("PLAN-001", response.getPlanId());
        assertEquals("ORDER-001", response.getOrderId());
        assertEquals("VEHICLE-001", response.getVehicleId());
        assertEquals("BMW i4", response.getVehicleModel());
        assertEquals("LINE-1", response.getProductionLine());
        assertEquals(Priority.HIGH, response.getPriority());
        assertEquals(LocalDate.of(2026, 8, 10), response.getPlannedStartDate());
        assertEquals(ProductionPlanStatus.PLANNED, response.getStatus());
        assertEquals(ProductionStage.CREATED, response.getCurrentStage());

        verify(productionPlanRepository).findByIdOptional("PLAN-001");
    }

    @Test
    void shouldThrowBusinessExceptionWhenPlanNotFound() {
        when(productionPlanRepository.findByIdOptional("PLAN-999")).thenReturn(Optional.empty());

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> productionPlanService.getById("PLAN-999"));

        assertEquals("PLAN_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("PLAN-999"));
    }

    @Test
    void shouldReturnAllPlans() {
        when(productionPlanRepository.listAll()).thenReturn(List.of(productionPlan));

        final List<ProductionPlanResponse> result = productionPlanService.getAll();

        assertEquals(1, result.size());
        assertEquals("PLAN-001", result.getFirst().getPlanId());
        assertEquals("ORDER-001", result.getFirst().getOrderId());
        assertEquals("VEHICLE-001", result.getFirst().getVehicleId());
        assertEquals("BMW i4", result.getFirst().getVehicleModel());
        assertEquals("LINE-1", result.getFirst().getProductionLine());
        assertEquals(Priority.HIGH, result.getFirst().getPriority());
        assertEquals(LocalDate.of(2026, 8, 10), result.getFirst().getPlannedStartDate());
        assertEquals(ProductionStage.CREATED, result.getFirst().getCurrentStage());

        verify(productionPlanRepository).listAll();
    }

    @Test
    void shouldThrowBusinessExceptionWhenNoProductionLineAvailable() {
        when(productionLineRepository.findLeastLoadedLineForUpdate()).thenReturn(Optional.empty());

        final CreateProductionPlanRequest request = new CreateProductionPlanRequest();

        request.setOrderId("ORDER-001");
        request.setVehicleId("VEHICLE-001");
        request.setVehicleModel("BMW i4");
        request.setPriority(Priority.HIGH);

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> productionPlanService.create(request));

        assertEquals(PlanningServiceExceptions.NO_PRODUCTION_LINE_AVAILABLE, exception.getErrorCode());
        assertEquals(503, exception.getStatusCode());
        assertEquals("No enabled production line available", exception.getMessage());

        verify(productionPlanRepository, never()).persist(any(ProductionPlan.class));
        verify(planningEventProducer, never()).publishProductionPlanned(any());
    }

    @Test
    void shouldThrowBusinessExceptionWhenProductionLineCapacityExceeded() {
        productionLine.setCurrentLoad(5);
        productionLine.setMaximumCapacity(5);

        when(productionLineRepository.findLeastLoadedLineForUpdate()).thenReturn(Optional.of(productionLine));

        final CreateProductionPlanRequest request = new CreateProductionPlanRequest();

        request.setOrderId("ORDER-001");
        request.setVehicleId("VEHICLE-001");
        request.setVehicleModel("BMW i4");
        request.setPriority(Priority.HIGH);

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> productionPlanService.create(request));

        assertEquals(PlanningServiceExceptions.PRODUCTION_LINE_CAPACITY_EXCEEDED, exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());

        verify(productionPlanRepository, never()).persist(any(ProductionPlan.class));
        verify(planningEventProducer, never()).publishProductionPlanned(any(ProductionPlannedPayload.class));
        verify(productionLineRepository, never()).incrementLoad(any(ProductionLine.class));
    }

    @Test
    void shouldAdvanceVehicleStageSuccessfully() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEHICLE-001", "ORDER-001", ProductionStage.CREATED,
                        ProductionStage.BODY, java.time.Instant.now());

        productionPlanService.advanceVehicleStage("EVT-STAGE-001", payload);

        assertEquals(ProductionStage.BODY, productionPlan.getCurrentStage());

        verify(productionPlanRepository).findByVehicleId("VEHICLE-001");
    }

    @Test
    void shouldIgnoreDuplicateVehicleStageAdvancedEvent() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEHICLE-001", "ORDER-001", ProductionStage.CREATED,
                        ProductionStage.BODY, java.time.Instant.now());

        productionPlanService.advanceVehicleStage("EVT-STAGE-001", payload);

        verify(productionPlanRepository, never()).findByVehicleId(anyString());
    }

    @Test
    void shouldThrowBusinessExceptionWhenProductionPlanDoesNotExistForVehicle() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-999")).thenReturn(Optional.empty());

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEHICLE-999", "ORDER-999", ProductionStage.CREATED,
                        ProductionStage.BODY, java.time.Instant.now());

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> productionPlanService.advanceVehicleStage("EVT-STAGE-002", payload));

        assertEquals(PlanningServiceExceptions.PLAN_NOT_FOUND, exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("VEHICLE-999"));
    }

    @Test
    void shouldThrowBusinessExceptionWhenStageTransitionSkipsStage() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEHICLE-001", "ORDER-001", ProductionStage.CREATED,
                        ProductionStage.PAINT, java.time.Instant.now());

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> productionPlanService.advanceVehicleStage("EVT-STAGE-003", payload));

        assertEquals(PlanningServiceExceptions.INVALID_STAGE_TRANSITION, exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());
        assertEquals(ProductionStage.CREATED, productionPlan.getCurrentStage());
    }

    @Test
    void shouldThrowBusinessExceptionWhenEventPreviousStageDoesNotMatchPlan() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEHICLE-001", "ORDER-001", ProductionStage.BODY, ProductionStage.PAINT,
                        java.time.Instant.now());

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> productionPlanService.advanceVehicleStage("EVT-STAGE-004", payload));

        assertEquals(PlanningServiceExceptions.INVALID_STAGE_TRANSITION, exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("expects stage BODY"));
        assertEquals(ProductionStage.CREATED, productionPlan.getCurrentStage());
    }

    @Test
    void shouldRejectTransitionFromFinalToAssembledWhenPlanIsAtDifferentStage() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        productionPlan.setCurrentStage(ProductionStage.POWERTRAIN);

        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEHICLE-001", "ORDER-001", ProductionStage.FINAL,
                        ProductionStage.ASSEMBLED, java.time.Instant.now());

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> productionPlanService.advanceVehicleStage("EVT-STAGE-005", payload));

        assertEquals(PlanningServiceExceptions.INVALID_STAGE_TRANSITION, exception.getErrorCode());
        assertEquals(409, exception.getStatusCode());
        assertEquals(ProductionStage.POWERTRAIN, productionPlan.getCurrentStage());
    }

    /**
     * Rework loop: Assembly rewinds the vehicle to an earlier stage without emitting a stage-advanced
     * event for the rewind itself, so the plan legitimately runs AHEAD of the incoming event.
     * Planning must re-synchronise instead of failing, otherwise the rest of the rework would be
     * pushed into the DLQ.
     */
    @Test
    void shouldResyncWhenPlanIsAheadOfEventAfterRework() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // The plan finished the first pass; quality then sent the vehicle back to PAINT.
        productionPlan.setCurrentStage(ProductionStage.ASSEMBLED);

        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));

        final VehicleStageAdvancedPayload payload =
                new VehicleStageAdvancedPayload("VEHICLE-001", "ORDER-001", ProductionStage.PAINT,
                        ProductionStage.INTERIOR, java.time.Instant.now());

        assertDoesNotThrow(() -> productionPlanService.advanceVehicleStage("EVT-STAGE-REWORK-001", payload));

        assertEquals(ProductionStage.INTERIOR, productionPlan.getCurrentStage());
    }

    @Test
    void shouldCompleteProductionPlanSuccessfully() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));
        when(productionLineRepository.findByLineIdForUpdate("LINE-1")).thenReturn(Optional.of(productionLine));
        when(planningEventProducer.publishPlanningKpiUpdated(any(PlanningKpiUpdatedPayload.class))).thenReturn(
                CompletableFuture.completedFuture(null));

        productionLine.setCurrentLoad(1);

        doAnswer(invocation -> {
            final ProductionLine line = invocation.getArgument(0);

            if (line.getCurrentLoad() > 0) {
                line.setCurrentLoad(line.getCurrentLoad() - 1);
            }

            return null;
        }).when(productionLineRepository).decrementLoad(productionLine);

        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEHICLE-001", "ORDER-001", "BMW i4", "LINE-1", java.time.Instant.now());

        productionPlanService.completePlan("EVT-ASSEMBLED-001", payload);

        assertEquals(ProductionPlanStatus.COMPLETED, productionPlan.getStatus());
        assertNotNull(productionPlan.getCompletedAt());
        assertEquals(0, productionLine.getCurrentLoad());

        verify(productionLineRepository).decrementLoad(productionLine);
        verify(productionLineRepository).findByLineIdForUpdate("LINE-1");
        verify(planningEventProducer).publishPlanningKpiUpdated(any(PlanningKpiUpdatedPayload.class));
    }

    @Test
    void shouldIgnoreDuplicateVehicleAssembledEvent() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0);

        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEHICLE-001", "ORDER-001", "BMW i4", "LINE-1", java.time.Instant.now());

        productionPlanService.completePlan("EVT-ASSEMBLED-001", payload);

        verify(productionPlanRepository, never()).findByVehicleId(anyString());
        verify(productionLineRepository, never()).decrementLoad(any(ProductionLine.class));
        verify(planningEventProducer, never()).publishPlanningKpiUpdated(any(PlanningKpiUpdatedPayload.class));
    }

    @Test
    void shouldThrowBusinessExceptionWhenCompletingPlanForUnknownVehicle() {
        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-999")).thenReturn(Optional.empty());

        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEHICLE-999", "ORDER-999", "BMW i4", "LINE-1", java.time.Instant.now());

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> productionPlanService.completePlan("EVT-ASSEMBLED-UNKNOWN", payload));

        assertEquals(PlanningServiceExceptions.PLAN_NOT_FOUND, exception.getErrorCode());
        assertEquals(404, exception.getStatusCode());
        assertTrue(exception.getMessage().contains("VEHICLE-999"));

        verify(productionPlanRepository).findByVehicleId("VEHICLE-999");
        verify(productionLineRepository, never()).decrementLoad(any(ProductionLine.class));
        verify(planningEventProducer, never()).publishPlanningKpiUpdated(any(PlanningKpiUpdatedPayload.class));
    }

    @Test
    void shouldNotCompleteAlreadyCompletedProductionPlan() {
        productionPlan.setStatus(ProductionPlanStatus.COMPLETED);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));

        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEHICLE-001", "ORDER-001", "BMW i4", "LINE-1", java.time.Instant.now());

        productionPlanService.completePlan("EVT-ASSEMBLED-003", payload);

        verify(productionLineRepository, never()).decrementLoad(any(ProductionLine.class));
        verify(productionLineRepository, never()).findByLineIdForUpdate(anyString());
        verify(planningEventProducer, never()).publishPlanningKpiUpdated(any(PlanningKpiUpdatedPayload.class));
    }

    @Test
    void shouldPublishCorrectPlanningKpiAfterCompletingPlan() {
        productionLine.setCurrentLoad(3);
        productionLine.setMaximumCapacity(5);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);
        when(productionPlanRepository.findByVehicleId("VEHICLE-001")).thenReturn(Optional.of(productionPlan));
        when(productionLineRepository.findByLineIdForUpdate("LINE-1")).thenReturn(Optional.of(productionLine));
        when(planningEventProducer.publishPlanningKpiUpdated(any(PlanningKpiUpdatedPayload.class))).thenReturn(
                CompletableFuture.completedFuture(null));

        doAnswer(invocation -> {
            final ProductionLine line = invocation.getArgument(0);

            if (line.getCurrentLoad() > 0) {
                line.setCurrentLoad(line.getCurrentLoad() - 1);
            }

            return null;
        }).when(productionLineRepository).decrementLoad(productionLine);

        final VehicleAssembledPayload payload =
                new VehicleAssembledPayload("VEHICLE-001", "ORDER-001", "BMW i4", "LINE-1", java.time.Instant.now());

        productionPlanService.completePlan("EVT-ASSEMBLED-004", payload);

        final var payloadCaptor = org.mockito.ArgumentCaptor.forClass(PlanningKpiUpdatedPayload.class);

        verify(planningEventProducer).publishPlanningKpiUpdated(payloadCaptor.capture());

        final PlanningKpiUpdatedPayload kpiPayload = payloadCaptor.getValue();

        assertEquals("LINE-1", kpiPayload.productionLine());
        assertEquals(2, kpiPayload.currentLoad());
        assertEquals(5, kpiPayload.maximumCapacity());
    }
}