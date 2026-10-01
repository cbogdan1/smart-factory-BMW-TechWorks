package com.smartfactory.procurement.control.repository;

import com.smartfactory.procurement.entity.PurchaseOrderStatusHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PurchaseOrderStatusHistoryRepository implements PanacheRepositoryBase<PurchaseOrderStatusHistory, Long> {
}
