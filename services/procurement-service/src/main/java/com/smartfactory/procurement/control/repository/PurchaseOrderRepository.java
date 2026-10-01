package com.smartfactory.procurement.control.repository;

import java.util.Optional;

import com.smartfactory.procurement.entity.PurchaseOrder;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PurchaseOrderRepository implements PanacheRepositoryBase<PurchaseOrder, Long> {
    public Optional<PurchaseOrder> findByPurchaseOrderId(final String purchaseOrderId) {
        return find("purchaseOrderId", purchaseOrderId).firstResultOptional();
    }
}
