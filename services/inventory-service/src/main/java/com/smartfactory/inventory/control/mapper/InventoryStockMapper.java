package com.smartfactory.inventory.control.mapper;

import com.smartfactory.common.dto.inventory.CreateUpdateInventoryStockRequest;
import com.smartfactory.common.dto.inventory.InventoryStockResponse;
import com.smartfactory.inventory.entity.InventoryStock;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InventoryStockMapper {
    public static InventoryStock toEntity(final InventoryStock stock, final CreateUpdateInventoryStockRequest request) {
        if (request == null) {
            return null;
        }

        stock.setAvailableQuantity(request.availableQuantity);
        stock.setReservedQuantity(request.reservedQuantity);
        stock.setMinimumQuantity(request.minimumQuantity);
        stock.setMaximumQuantity(request.maximumQuantity);

        return stock;
    }

    public static InventoryStockResponse fromEntity(final InventoryStock stock) {
        final InventoryStockResponse response = new InventoryStockResponse();
        response.partId = stock.getPart().getPartId();
        response.availableQuantity = stock.getAvailableQuantity();
        response.reservedQuantity = stock.getReservedQuantity();
        response.minimumQuantity = stock.getMinimumQuantity();
        response.maximumQuantity = stock.getMaximumQuantity();

        return response;
    }
}
