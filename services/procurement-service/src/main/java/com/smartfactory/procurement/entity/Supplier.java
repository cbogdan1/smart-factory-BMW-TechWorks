package com.smartfactory.procurement.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false, unique = true)
    private String supplierId;

    @Size(min = 3, max = 255)
    @Column(nullable = false, unique = true)
    private String name;

    @Min(value = 0, message = "Lead time days cannot be negative.")
    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0.")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5.")
    @Column(name = "rating", nullable = false)
    private BigDecimal rating;

    @Column(name = "contact_email", nullable = false, unique = true)
    private String contactEmail;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDate.now();
    }

}
