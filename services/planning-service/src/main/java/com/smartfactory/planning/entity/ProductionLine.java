package com.smartfactory.planning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "production_line")
@Getter
@Setter
public class ProductionLine {
    @Id
    @Column(name = "line_id")
    private String lineId;

    @Column(name = "name")
    private String name;

    @Column(name = "maximum_capacity")
    private int maximumCapacity;

    @Column(name = "current_load")
    private int currentLoad;

    @Column(name = "enabled")
    private boolean enabled;
}
