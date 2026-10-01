package com.smartfactory.procurement.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import com.smartfactory.common.dto.procurement.CreatePurchaseOrderRequest;
import com.smartfactory.common.dto.procurement.CreatePurchaseOrderResponse;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineRequest;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineResponse;
import com.smartfactory.common.enums.PurchaseOrderStatus;
import com.smartfactory.procurement.control.service.PurchaseOrderService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PurchaseOrderResourceTest {

    private PurchaseOrderResource resource;

    private PurchaseOrderService service;

    @BeforeEach
    void setUp() {
        resource = new PurchaseOrderResource();

        service = mock(PurchaseOrderService.class);

        resource.purchaseOrderService = service;
    }

    @Test
    void shouldCreatePurchaseOrder() {

        CreatePurchaseOrderRequest request = new CreatePurchaseOrderRequest("PART-BAT-001", "SUP-001", 5);

        CreatePurchaseOrderResponse serviceResponse =
                new CreatePurchaseOrderResponse("PO-001", "PART-BAT-001", "SUP-001", 5, PurchaseOrderStatus.CREATED,
                        LocalDateTime.now());

        when(service.createPurchaseOrder(request)).thenReturn(serviceResponse);

        Response response = resource.createPurchaseOrder(request);

        assertEquals(201, response.getStatus());

        CreatePurchaseOrderResponse result = (CreatePurchaseOrderResponse) response.getEntity();

        assertNotNull(result);
        assertEquals("PO-001", result.purchaseOrderId());
        assertEquals("PART-BAT-001", result.partId());
        assertEquals("SUP-001", result.supplierId());
        assertEquals(5, result.quantity());
        assertEquals(PurchaseOrderStatus.CREATED, result.status());
    }

    @Test
    void shouldChangePurchaseOrderStatus() {

        PurchaseOrderStateMachineRequest request = new PurchaseOrderStateMachineRequest(PurchaseOrderStatus.SENT);

        PurchaseOrderStateMachineResponse serviceResponse =
                new PurchaseOrderStateMachineResponse("PO-001", "SUP-001", 5, PurchaseOrderStatus.SENT,
                        LocalDateTime.now());

        when(service.changeStatus("PO-001", request)).thenReturn(serviceResponse);

        Response response = resource.changeStatus("PO-001", request);

        assertEquals(200, response.getStatus());

        PurchaseOrderStateMachineResponse result = (PurchaseOrderStateMachineResponse) response.getEntity();

        assertNotNull(result);
        assertEquals("PO-001", result.purchaseOrderId());
        assertEquals("SUP-001", result.supplierId());
        assertEquals(5, result.quantity());
        assertEquals(PurchaseOrderStatus.SENT, result.status());
    }
}