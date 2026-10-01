package com.smartfactory.agv.entity.models;

import java.time.Instant;

import com.smartfactory.common.enums.MissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery_mission")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mission_id", nullable = false, unique = true)
    private String missionId;

    @Column(name = "material_request_event_id", nullable = false, unique = true)
    private String materialRequestEventId;

    @Column(name = "request_id", nullable = false)
    private String requestId;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String material;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "source_node_id", nullable = false)
    private String sourceNodeId;

    @Column(name = "target_node_id", nullable = false)
    private String targetNodeId;

    @Column(name = "planned_route", nullable = false, length = 2000)
    private String plannedRoute;

    @Column(name = "distance_meters", nullable = false)
    private Integer distanceMeters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "completed_date")
    private Instant completedDate;
}