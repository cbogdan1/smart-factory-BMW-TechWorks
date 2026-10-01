package com.smartfactory.agv.entity.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "edge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "edge_id", unique = true, nullable = false)
    private String edgeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_node_id", referencedColumnName = "node_id", nullable = false)
    private Node sourceNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_node_id", referencedColumnName = "node_id", nullable = false)
    private Node targetNode;

    @Column(name = "distance_meters", nullable = false)
    private Integer distanceMeters;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
}