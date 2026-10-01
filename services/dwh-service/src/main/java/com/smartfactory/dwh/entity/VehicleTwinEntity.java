package com.smartfactory.dwh.entity;

import java.time.Instant;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle_twin")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTwinEntity extends PanacheEntityBase {
    @Id
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "status")
    private String status;

    @Column(name = "current_stage")
    private String currentStage;

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "quality_status")
    private String qualityStatus;

    @Column(name = "rework_count")
    private Integer reworkCount;

    @Column(name = "last_updated")
    private Instant lastUpdated;
}
