package com.smartfactory.common.dto.procurement;

public record CreatePurchaseOrderRequest(String partId, String supplierId, Integer quantity) {
}
