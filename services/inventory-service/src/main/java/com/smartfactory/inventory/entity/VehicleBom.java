package com.smartfactory.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicle_bom")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleBom {
    @Id
    @Column(name = "bom_id", nullable = false)
    private String id;

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @ManyToOne
    @JoinColumn( name = "part_code", referencedColumnName = "part_code", nullable = false )
    private Part part;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}
