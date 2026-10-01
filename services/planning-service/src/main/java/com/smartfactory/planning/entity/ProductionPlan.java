package com.smartfactory.planning.entity;

import com.smartfactory.common.enums.Priority;
import com.smartfactory.common.enums.ProductionPlanStatus;
import com.smartfactory.common.enums.ProductionStage;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "production_plan")
@Getter
@Setter
public class ProductionPlan {
    @Id
    @Column(name = "plan_id")
    private String planId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "production_line")
    private String productionLine;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductionPlanStatus status;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @PrePersist
    private void generateId() {
        if (planId == null) {
            planId = "PLAN-" + UUID.randomUUID();
        }

        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", nullable = false)
    private ProductionStage currentStage = ProductionStage.CREATED;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}