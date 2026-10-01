package com.smartfactory.inventory.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import com.smartfactory.common.enums.DemandStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "material_demands")
public class MaterialDemand extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "demand_id", nullable = false, unique = true)
    private String demandId;

    @Column(name = "plan_id", nullable = false)
    private String planId;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_code", referencedColumnName = "part_code", nullable = false)
    private Part requiredPart;

    @Column(name = "required_quantity", nullable = false)
    private Integer requiredQuantity;

    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DemandStatus status = DemandStatus.PLANNED;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @Column(name = "event_id", nullable = false)
    private String eventId;
}
