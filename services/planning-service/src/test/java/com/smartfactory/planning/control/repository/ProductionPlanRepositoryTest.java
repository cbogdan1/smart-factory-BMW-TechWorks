package com.smartfactory.planning.control.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionPlanStatus;
import com.smartfactory.planning.entity.ProductionPlan;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Transactional
class ProductionPlanRepositoryTest {
    @Inject
    ProductionPlanRepository repository;

    @Test
    void shouldPersistProductionPlan() {
        final ProductionPlan plan = createPlan("PLAN-000001");

        repository.persist(plan);
        repository.flush();

        final Optional<ProductionPlan> result = repository.findByIdOptional("PLAN-000001");

        assertTrue(result.isPresent());

        final ProductionPlan persisted = result.get();

        assertEquals("ORD-000001", persisted.getOrderId());
        assertEquals("VEH-000001", persisted.getVehicleId());
        assertEquals("BMW i4", persisted.getVehicleModel());
        assertEquals("LINE-1", persisted.getProductionLine());
        assertEquals(Priority.HIGH, persisted.getPriority());
        assertEquals(LocalDate.of(2026, 8, 10), persisted.getPlannedStartDate());
        assertEquals(ProductionPlanStatus.PLANNED, persisted.getStatus());
    }

    @Test
    void shouldFindById() {
        final ProductionPlan plan = createPlan("PLAN-000002");

        repository.persist(plan);
        repository.flush();

        final Optional<ProductionPlan> result = repository.findByIdOptional("PLAN-000002");

        assertTrue(result.isPresent());
        assertEquals("PLAN-000002", result.get().getPlanId());
        assertEquals("BMW i4", result.get().getVehicleModel());
    }

    @Test
    void shouldReturnEmptyWhenPlanDoesNotExist() {
        final Optional<ProductionPlan> result = repository.findByIdOptional("DOES-NOT-EXIST");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteProductionPlan() {
        final ProductionPlan plan = createPlan("PLAN-000003");

        repository.persist(plan);
        repository.flush();

        repository.delete(plan);
        repository.flush();

        final Optional<ProductionPlan> result = repository.findByIdOptional("PLAN-000003");

        assertTrue(result.isEmpty());
    }

    private ProductionPlan createPlan(final String id) {
        final ProductionPlan plan = new ProductionPlan();

        plan.setPlanId(id);
        plan.setOrderId("ORD-000001");
        plan.setVehicleId("VEH-000001");
        plan.setVehicleModel("BMW i4");
        plan.setProductionLine("LINE-1");
        plan.setPriority(Priority.HIGH);
        plan.setPlannedStartDate(LocalDate.of(2026, 8, 10));
        plan.setStatus(ProductionPlanStatus.PLANNED);
        plan.setCreatedDate(LocalDateTime.now());

        return plan;
    }
}