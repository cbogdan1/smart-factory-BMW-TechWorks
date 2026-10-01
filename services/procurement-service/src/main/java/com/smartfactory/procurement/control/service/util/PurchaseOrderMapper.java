package com.smartfactory.procurement.control.service.util;

import com.smartfactory.common.dto.procurement.CreatePurchaseOrderResponse;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineResponse;
import com.smartfactory.procurement.entity.PurchaseOrder;
import com.smartfactory.procurement.entity.PurchaseOrderStatusHistory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PurchaseOrderMapper {
    public CreatePurchaseOrderResponse mapToPurchaseOrderResponse(final PurchaseOrder entity) {
        return new CreatePurchaseOrderResponse(entity.getPurchaseOrderId(), entity.getPartId(), entity.getSupplierId(),
                entity.getQuantity(), entity.getStatus(), entity.getOrderDate());
    }

    public PurchaseOrderStateMachineResponse mapToPurchaseOrderStateMachineResponse(
            final PurchaseOrderStatusHistory purchaseOrderStatusHistory, final PurchaseOrder purchaseOrder) {
        return new PurchaseOrderStateMachineResponse(purchaseOrder.getPurchaseOrderId(), purchaseOrder.getSupplierId(),
                purchaseOrder.getQuantity(), purchaseOrder.getStatus(),
                purchaseOrderStatusHistory.getLastModifiedDate());
    }
}
