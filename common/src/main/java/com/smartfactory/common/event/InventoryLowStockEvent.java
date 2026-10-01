package com.smartfactory.common.event;

import com.smartfactory.common.payloads.inventory_service.InventoryLowStockPayload;

public record InventoryLowStockEvent(DomainEvent<InventoryLowStockPayload> event) {}
