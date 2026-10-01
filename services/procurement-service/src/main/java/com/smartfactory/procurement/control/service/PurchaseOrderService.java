package com.smartfactory.procurement.control.service;

import com.smartfactory.common.dto.procurement.CreatePurchaseOrderRequest;
import com.smartfactory.common.dto.procurement.CreatePurchaseOrderResponse;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineRequest;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineResponse;

public interface PurchaseOrderService {
    public CreatePurchaseOrderResponse createPurchaseOrder(final CreatePurchaseOrderRequest request);

    public PurchaseOrderStateMachineResponse changeStatus(final String purchaseOrderId,
            final PurchaseOrderStateMachineRequest request);

    public void markEventProcessed(final String purchaseOrderId);
}
