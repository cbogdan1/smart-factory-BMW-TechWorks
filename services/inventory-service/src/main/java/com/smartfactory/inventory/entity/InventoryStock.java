package com.smartfactory.inventory.entity;

import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Inventory Stock Entity mapped to the table "inventory_stock"
 * <br><br>
 * <p>
 * <b>Unique constraint</b> (for any other field different from the primary key): {@code part_id}
 * </p>
 * <p>-> needed in order to avoid multiple records for a single part</p>
 * <br>
 * <b>Fields</b>: id, partId, availableQuantity, reservedQuantity, minimumQuantity, maximumQuantity
 * </p>
 */
@Entity
@Table(name = "inventory_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStock {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @OneToOne
    @JoinColumn(
            name = "part_id",
            nullable = false,
            unique = true
    )
    private Part part;

    @Column(name = "available_quantity", nullable = false)
    private Long availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    private Long reservedQuantity;

    @Column(name = "minimum_quantity", nullable = false)
    private Long minimumQuantity;

    @Column(name = "maximum_quantity", nullable = false)
    private Long maximumQuantity;
}
