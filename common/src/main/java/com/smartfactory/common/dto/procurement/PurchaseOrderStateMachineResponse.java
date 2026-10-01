package com.smartfactory.common.dto.procurement;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.PurchaseOrderStatus;

public record PurchaseOrderStateMachineResponse(String purchaseOrderId, String supplierId, Integer quantity,
                                                PurchaseOrderStatus status, LocalDateTime lastModifiedDate) {

}
