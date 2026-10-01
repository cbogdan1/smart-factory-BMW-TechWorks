package com.smartfactory.quality.entity;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.InspectionStatus;
import com.smartfactory.common.enums.ProductionStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inspection")
public class Inspection {
    @Id
    @Column(name = "inspection_id", nullable = false, length = 50)
    private String inspectionId;

    @Column(name = "vehicle_id", nullable = false, length = 100)
    private String vehicleId;

    @Column(name = "inspection_date", nullable = false)
    private LocalDateTime inspectionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InspectionStatus status;

    @Column(name = "score")
    private Integer score;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", length = 30)
    private Decision decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_stage", length = 30)
    private ProductionStage targetStage;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "inspector", length = 200)
    private String inspector;
}
