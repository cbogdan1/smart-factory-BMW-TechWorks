package com.smartfactory.inventory.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.common.dto.inventory.CreateUpdateInventoryStockRequest;
import com.smartfactory.common.dto.inventory.InventoryStockResponse;
import com.smartfactory.inventory.control.InventoryStockService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InventoryResourceTest {

    @Mock
    InventoryStockService inventoryStockService;

    @InjectMocks
    InventoryResource inventoryResource;

    @Test
    void shouldUpdateStock() {
        String partId = "PART-1";
        CreateUpdateInventoryStockRequest request = new CreateUpdateInventoryStockRequest();
        InventoryStockResponse mockResponse = new InventoryStockResponse();
        when(inventoryStockService.updateStock(partId, request)).thenReturn(mockResponse);

        Response response = inventoryResource.updateStock(partId, request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockResponse, response.getEntity());
        verify(inventoryStockService).updateStock(partId, request);
    }

    @Test
    void shouldGetStockForPart() {
        String partId = "PART-1";
        InventoryStockResponse mockResponse = new InventoryStockResponse();
        when(inventoryStockService.getStockForPart(partId)).thenReturn(mockResponse);

        Response response = inventoryResource.getStockForPart(partId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockResponse, response.getEntity());
    }

    @Test
    void shouldGetAllStock() {
        List<InventoryStockResponse> mockResponses = List.of(new InventoryStockResponse());
        when(inventoryStockService.getAllStock()).thenReturn(mockResponses);

        Response response = inventoryResource.getAllStock();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(mockResponses, response.getEntity());
    }
}