package com.smartfactory.order.entity;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.BatteryType;
import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.common.enums.Priority;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends PanacheEntityBase {

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @Column(name = "vehicle_id", nullable = false, unique = true)
    private String vehicleId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "vehicle_model", nullable = false)
    private String vehicleModel;

    @Column(name = "color", nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "battery_type", nullable = false)
    private BatteryType batteryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date", nullable = false)
    private LocalDateTime lastModifiedDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;
}
