package com.smartfactory.inventory.control.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.smartfactory.common.dto.inventory.CreateUpdateInventoryStockRequest;
import com.smartfactory.common.dto.inventory.InventoryStockResponse;
import com.smartfactory.inventory.entity.InventoryStock;
import com.smartfactory.inventory.entity.Part;
import org.junit.jupiter.api.Test;

class InventoryStockMapperTest {

    @Test
    void shouldMapToEntity() {
        InventoryStock stock = new InventoryStock();
        CreateUpdateInventoryStockRequest request = new CreateUpdateInventoryStockRequest();
        request.availableQuantity = 100L;
        request.reservedQuantity = 20L;
        request.minimumQuantity = 10L;
        request.maximumQuantity = 200L;

        InventoryStock result = InventoryStockMapper.toEntity(stock, request);

        assertEquals(100L, result.getAvailableQuantity());
        assertEquals(20L, result.getReservedQuantity());
        assertEquals(10L, result.getMinimumQuantity());
        assertEquals(200L, result.getMaximumQuantity());
    }

    @Test
    void shouldReturnNullWhenMappingNullRequestToEntity() {
        assertNull(InventoryStockMapper.toEntity(new InventoryStock(), null));
    }

    @Test
    void shouldMapFromEntity() {
        Part part = new Part();
        part.setPartId("PART-001");

        InventoryStock stock = new InventoryStock();
        stock.setPart(part);
        stock.setAvailableQuantity(100L);
        stock.setReservedQuantity(20L);
        stock.setMinimumQuantity(10L);
        stock.setMaximumQuantity(200L);

        InventoryStockResponse result = InventoryStockMapper.fromEntity(stock);

        assertEquals("PART-001", result.partId);
        assertEquals(100L, result.availableQuantity);
        assertEquals(20L, result.reservedQuantity);
        assertEquals(10L, result.minimumQuantity);
        assertEquals(200L, result.maximumQuantity);
    }
}