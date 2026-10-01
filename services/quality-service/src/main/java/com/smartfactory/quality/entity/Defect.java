package com.smartfactory.quality.entity;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.ProductionStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "defect")
public class Defect {

    @Id
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "defect_id", length = 50, unique = true)
    private String defectId;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "penalty_points", nullable = false)
    private Integer penaltyPoints;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "affected_stage", length = 30)
    private ProductionStage affectedStage;

}