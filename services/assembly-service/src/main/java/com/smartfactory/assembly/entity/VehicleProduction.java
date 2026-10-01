package com.smartfactory.assembly.entity;

import java.time.OffsetDateTime;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.enums.ProductionStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vehicle_production")
@Getter
@Setter
public class VehicleProduction extends PanacheEntityBase {

    @Id
    @Column(name = "vehicle_id", nullable = false, updatable = false)
    private String vehicleId;

    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_stage", nullable = false)
    private ProductionStage currentStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductionStatus status;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "started_event_id", unique = true)
    private String startedEventId;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "production_line")
    private String productionLine;

    @Column(name = "rework_count", nullable = false)
    private Integer reworkCount = 0;
}
