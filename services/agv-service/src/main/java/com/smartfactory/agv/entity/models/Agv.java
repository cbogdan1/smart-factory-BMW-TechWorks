package com.smartfactory.agv.entity.models;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.AgvStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agv")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Agv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agv_id", nullable = false, unique = true)
    private String agvId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgvStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_node_id", referencedColumnName = "node_id", nullable = false)
    private Node currentNode;

    @Column(name = "battery_level", nullable = false)
    private Integer batteryLevel;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    void onCreate() {
        final LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        lastUpdated = now;
    }

    @PreUpdate
    void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}