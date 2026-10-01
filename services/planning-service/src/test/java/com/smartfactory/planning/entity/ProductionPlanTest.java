package com.smartfactory.planning.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionPlanStatus;
import com.smartfactory.common.enums.ProductionStage;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;

class ProductionPlanTest {
    @Nonnull
    private static ProductionPlan createPlan(final LocalDateTime createdDate) {
        final ProductionPlan plan = new ProductionPlan();

        plan.setPlanId("PLAN-000001");
        plan.setOrderId("ORD-000001");
        plan.setVehicleId("VEH-000001");
        plan.setVehicleModel("BMW i4");
        plan.setProductionLine("LINE-1");
        plan.setPriority(Priority.HIGH);
        plan.setPlannedStartDate(LocalDate.of(2026, 8, 10));
        plan.setStatus(ProductionPlanStatus.PLANNED);
        plan.setCurrentStage(ProductionStage.CREATED);
        plan.setCreatedDate(createdDate);

        return plan;
    }

    @Test
    void shouldSetAndGetAllFields() {
        final LocalDateTime createdDate = LocalDateTime.now();
        final ProductionPlan plan = createPlan(createdDate);

        assertEquals("PLAN-000001", plan.getPlanId());
        assertEquals("ORD-000001", plan.getOrderId());
        assertEquals("VEH-000001", plan.getVehicleId());
        assertEquals("BMW i4", plan.getVehicleModel());
        assertEquals("LINE-1", plan.getProductionLine());
        assertEquals(Priority.HIGH, plan.getPriority());
        assertEquals(LocalDate.of(2026, 8, 10), plan.getPlannedStartDate());
        assertEquals(ProductionPlanStatus.PLANNED, plan.getStatus());
        assertEquals(ProductionStage.CREATED, plan.getCurrentStage());
        assertEquals(createdDate, plan.getCreatedDate());
    }

    @Test
    void shouldCreateEmptyEntity() {
        final ProductionPlan plan = new ProductionPlan();

        assertNotNull(plan);
        assertNull(plan.getPlanId());
        assertNull(plan.getOrderId());
        assertNull(plan.getVehicleId());
        assertNull(plan.getVehicleModel());
        assertNull(plan.getProductionLine());
        assertNull(plan.getPriority());
        assertNull(plan.getPlannedStartDate());
        assertNull(plan.getStatus());
        assertNull(plan.getCreatedDate());
        assertEquals(ProductionStage.CREATED, plan.getCurrentStage());
    }
}