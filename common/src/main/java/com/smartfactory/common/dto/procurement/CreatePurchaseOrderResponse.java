package com.smartfactory.common.dto.procurement;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.PurchaseOrderStatus;

public record CreatePurchaseOrderResponse(String purchaseOrderId, String partId, String supplierId, Integer quantity,
                                          PurchaseOrderStatus status, LocalDateTime orderDate) {
}
