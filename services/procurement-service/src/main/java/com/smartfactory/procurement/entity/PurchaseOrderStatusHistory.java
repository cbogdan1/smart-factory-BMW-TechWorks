package com.smartfactory.procurement.entity;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.PurchaseOrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "purchase_order_status_history")
public class PurchaseOrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "purchase_order_id")
    private String purchaseOrderId;
    @Column(name = "old_status")
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus oldStatus;
    @Column(name = "new_status")
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus newStatus;
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}
