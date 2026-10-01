package com.smartfactory.common.dto.inventory;

public class InventoryStockResponse {
    public String partId;
    public Long availableQuantity;
    public Long reservedQuantity;
    public Long minimumQuantity;
    public Long maximumQuantity;
}
