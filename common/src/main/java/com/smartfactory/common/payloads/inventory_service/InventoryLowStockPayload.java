package com.smartfactory.common.payloads.inventory_service;

public record InventoryLowStockPayload(String partId, String partCode, long availableQuantity, long minimumQuantity,
                                       String detectedAt) {
}
