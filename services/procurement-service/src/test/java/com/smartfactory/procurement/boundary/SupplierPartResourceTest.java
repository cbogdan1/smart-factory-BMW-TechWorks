package com.smartfactory.procurement.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.smartfactory.common.dto.procurement.CreateSupplierPartRequest;
import com.smartfactory.common.dto.procurement.SupplierPartResponse;
import com.smartfactory.common.dto.procurement.SuppliersForPartResponse;
import com.smartfactory.procurement.control.service.SupplierPartService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SupplierPartResourceTest {

    private SupplierPartResource resource;

    private SupplierPartService service;

    @BeforeEach
    void setUp() {
        resource = new SupplierPartResource();

        service = mock(SupplierPartService.class);

        resource.service = service;
    }

    @Test
    void shouldGetAllSupplierParts() {

        final SupplierPartResponse supplierPart =
                new SupplierPartResponse(1L, "SUP001", "PART001", BigDecimal.valueOf(10.50), LocalDate.now());

        when(service.getAllSupplierParts()).thenReturn(List.of(supplierPart));

        final Response response = resource.getAllSupplierParts();

        assertEquals(200, response.getStatus());

        final List<SupplierPartResponse> result = (List<SupplierPartResponse>) response.getEntity();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUP001", result.get(0).supplierId());
    }

    @Test
    void shouldGetSupplierPartById() {

        final SupplierPartResponse supplierPart =
                new SupplierPartResponse(1L, "SUP001", "PART001", BigDecimal.valueOf(10.50), LocalDate.now());

        when(service.getSupplierPartById(1L)).thenReturn(supplierPart);

        final Response response = resource.getSupplierPartById(1L);

        assertEquals(200, response.getStatus());

        final SupplierPartResponse result = (SupplierPartResponse) response.getEntity();

        assertNotNull(result);
        assertEquals(1L, result.supplierPartId());
        assertEquals("SUP001", result.supplierId());
    }

    @Test
    void shouldCreateSupplierPartRelationship() {

        final CreateSupplierPartRequest request =
                new CreateSupplierPartRequest("SUP001", "PART001", BigDecimal.valueOf(10.50), 5);

        final SupplierPartResponse supplierPart =
                new SupplierPartResponse(1L, "SUP001", "PART001", BigDecimal.valueOf(10.50), LocalDate.now());

        when(service.createRelationship(request)).thenReturn(supplierPart);

        final Response response = resource.createSupplierPart(request);

        assertEquals(201, response.getStatus());

        final SupplierPartResponse result = (SupplierPartResponse) response.getEntity();

        assertNotNull(result);
        assertEquals(1L, result.supplierPartId());
        assertEquals("SUP001", result.supplierId());
    }

    @Test
    void shouldGetSuppliersForGivenPart() {

        final SuppliersForPartResponse supplier =
                new SuppliersForPartResponse("SUP001", "Supplier One", 3, new BigDecimal("4.80"),
                        new BigDecimal("10.50"));

        when(service.getSuppliersOrderedByLeadTimeAsc("PART001")).thenReturn(List.of(supplier));

        final Response response = resource.getSuppliersForGivenPart("PART001");

        assertEquals(200, response.getStatus());

        final List<SuppliersForPartResponse> result = (List<SuppliersForPartResponse>) response.getEntity();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SUP001", result.get(0).supplierId());
        assertEquals("Supplier One", result.get(0).name());
        assertEquals(3, result.get(0).leadTimeDays());
        assertEquals(new BigDecimal("4.80"), result.get(0).rating());
        assertEquals(new BigDecimal("10.50"), result.get(0).unitCost());
    }
}