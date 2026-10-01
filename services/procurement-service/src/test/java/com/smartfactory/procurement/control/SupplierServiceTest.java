package com.smartfactory.procurement.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import com.smartfactory.common.dto.procurement.CreateSupplierRequest;
import com.smartfactory.common.dto.procurement.SupplierResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.procurement.control.repository.SupplierRepository;
import com.smartfactory.procurement.control.service.SupplierServiceImpl;
import com.smartfactory.procurement.entity.Supplier;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class SupplierServiceTest {

    @Inject
    SupplierServiceImpl supplierService;

    @InjectMock
    SupplierRepository supplierRepository;

    @Test
    void createSupplier_SuccessfulCreation_GeneratesSupId() {
        // Arrange
        final CreateSupplierRequest request =
                new CreateSupplierRequest("Acme Corp", 14, new BigDecimal("4.5"), "contact@acme.com", true);
        when(supplierRepository.findByContactEmail(request.contactEmail())).thenReturn(Optional.empty());

        // Act
        final SupplierResponse response = supplierService.createSupplier(request);

        // Assert
        assertNotNull(response);
        assertTrue(response.supplierId().startsWith("SUP-"));
        assertEquals("Acme Corp", response.name());
        assertEquals("contact@acme.com", response.contactEmail());

        // Verify repository was called to persist
        Mockito.verify(supplierRepository, Mockito.times(1)).persist(any(Supplier.class));
    }

    @Test
    void createSupplier_DuplicateEmail_ThrowsException() {
        // Arrange
        final CreateSupplierRequest request =
                new CreateSupplierRequest("Acme Corp", 14, new BigDecimal("4.5"), "contact@acme.com", true);
        final Supplier existingSupplier = new Supplier();
        when(supplierRepository.findByContactEmail(request.contactEmail())).thenReturn(Optional.of(existingSupplier));

        // Act & Assert
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> supplierService.createSupplier(request));

        assertEquals("A supplier with this email already exists.", exception.getMessage());
        assertEquals("A supplier with this email already exists.", exception.getMessage());
        Mockito.verify(supplierRepository, Mockito.never()).persist(any(Supplier.class));
    }

    @Test
    void getSupplierById_SupplierExists_ReturnsDto() {
        // Arrange
        final String id = "SUP-12345678";
        final Supplier supplier = new Supplier();
        supplier.setSupplierId(id);
        supplier.setName("TechParts");
        when(supplierRepository.findBySupplierId(id)).thenReturn(Optional.of(supplier));

        // Act
        final SupplierResponse response = supplierService.getSupplierById(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.supplierId());
        assertEquals("TechParts", response.name());
    }
}