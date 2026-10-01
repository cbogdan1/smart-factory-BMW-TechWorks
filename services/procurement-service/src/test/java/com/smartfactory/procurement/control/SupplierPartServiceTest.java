package com.smartfactory.procurement.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.smartfactory.common.dto.procurement.SupplierPartResponse;
import com.smartfactory.common.dto.procurement.SuppliersForPartResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.service.SupplierPartServiceImpl;
import com.smartfactory.procurement.entity.Supplier;
import com.smartfactory.procurement.entity.SupplierPart;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SupplierPartServiceTest {

    @InjectMocks
    private SupplierPartServiceImpl control;

    @Mock
    private SupplierPartRepository supplierPartRepository;

    @Mock
    private InventoryClient inventoryClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllSupplierParts() {

        final Supplier supplier = new Supplier();
        supplier.setSupplierId("SUP001");

        final SupplierPart supplierPart = new SupplierPart();
        supplierPart.setSupplierPartId(1L);
        supplierPart.setSupplier(supplier);
        supplierPart.setPartId("PART001");
        supplierPart.setUnitCost(BigDecimal.TEN);
        supplierPart.setCreatedDate(LocalDate.now());

        when(supplierPartRepository.listAll()).thenReturn(List.of(supplierPart));

        final List<SupplierPartResponse> result = control.getAllSupplierParts();

        assertEquals(1, result.size());
        assertEquals("SUP001", result.getFirst().supplierId());
        assertEquals("PART001", result.getFirst().partCode());
    }

    @Test
    void shouldReturnSupplierPartById() {

        final Supplier supplier = new Supplier();
        supplier.setSupplierId("SUP001");

        final SupplierPart supplierPart = new SupplierPart();
        supplierPart.setSupplierPartId(1L);
        supplierPart.setSupplier(supplier);
        supplierPart.setPartId("PART001");
        supplierPart.setUnitCost(new BigDecimal("20.00"));

        when(supplierPartRepository.findByIdOptional(1L)).thenReturn(Optional.of(supplierPart));

        final SupplierPartResponse result = control.getSupplierPartById(1L);

        assertNotNull(result);
        assertEquals(1L, result.supplierPartId());
        assertEquals("SUP001", result.supplierId());
        assertEquals("PART001", result.partCode());
    }

    @Test
    void shouldThrowWhenSupplierPartNotFound() {

        when(supplierPartRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> control.getSupplierPartById(99L));
    }

    @Test
    void shouldReturnSuppliersOrderedByLeadTime() {

        final Supplier supplier = new Supplier();
        supplier.setSupplierId("SUP001");
        supplier.setName("Supplier One");
        supplier.setRating(new BigDecimal("4.80"));

        final SupplierPart supplierPart = new SupplierPart();
        supplierPart.setSupplierPartId(1L);
        supplierPart.setSupplier(supplier);
        supplierPart.setPartId("PART001");
        supplierPart.setLeadTimeDays(3);
        supplierPart.setUnitCost(new BigDecimal("10.50"));

        when(inventoryClient.getPartById("PART001")).thenReturn(Response.ok().build());

        when(supplierPartRepository.findPreferredSuppliersByPartId("PART001")).thenReturn(List.of(supplierPart));

        final List<SuppliersForPartResponse> result = control.getSuppliersOrderedByLeadTimeAsc("PART001");

        assertEquals(1, result.size());
        assertEquals("SUP001", result.getFirst().supplierId());
    }

    @Test
    void shouldThrowWhenPartDoesNotExist() {

        final WebApplicationException exception =
                new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());

        when(inventoryClient.getPartById("UNKNOWN")).thenThrow(exception);

        assertThrows(BusinessException.class, () -> control.getSuppliersOrderedByLeadTimeAsc("UNKNOWN"));
    }
}