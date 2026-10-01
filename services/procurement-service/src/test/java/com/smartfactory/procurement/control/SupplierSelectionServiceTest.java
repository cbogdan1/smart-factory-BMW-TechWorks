package com.smartfactory.procurement.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import com.smartfactory.common.dto.procurement.SupplierSelectionResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.service.SupplierSelectionServiceImpl;
import com.smartfactory.procurement.entity.Supplier;
import com.smartfactory.procurement.entity.SupplierPart;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SupplierSelectionServiceTest {

    @Inject
    SupplierSelectionServiceImpl supplierSelectionService;

    @InjectMock
    SupplierPartRepository supplierPartRepository;

    @Test
    void selectBest_LowestLeadTime_Wins() {

        final SupplierPart supplier1 = createSupplierPart("SUP-001", 5, 4.8, 200);
        final SupplierPart supplier2 = createSupplierPart("SUP-002", 2, 4.2, 220);
        final SupplierPart supplier3 = createSupplierPart("SUP-003", 3, 4.9, 180);

        // Repository returns results already sorted
        when(supplierPartRepository.findPreferredSuppliersByPartId("PART-BAT-001")).thenReturn(
                List.of(supplier2, supplier3, supplier1));

        final SupplierSelectionResponse response = supplierSelectionService.selectBest("PART-BAT-001");

        assertEquals("SUP-002", response.selectedSupplierId());
    }

    @Test
    void selectBest_SameLeadTime_HigherRating_Wins() {

        final SupplierPart supplier1 = createSupplierPart("SUP-001", 2, 4.2, 220);
        final SupplierPart supplier2 = createSupplierPart("SUP-003", 2, 4.5, 210);

        // Higher rating first
        when(supplierPartRepository.findPreferredSuppliersByPartId("PART-BAT-001")).thenReturn(
                List.of(supplier2, supplier1));

        final SupplierSelectionResponse response = supplierSelectionService.selectBest("PART-BAT-001");

        assertEquals("SUP-003", response.selectedSupplierId());
    }

    @Test
    void selectBest_SameLeadTimeAndRating_LowerCost_Wins() {

        final SupplierPart supplier1 = createSupplierPart("SUP-001", 2, 4.5, 180);
        final SupplierPart supplier2 = createSupplierPart("SUP-002", 2, 4.5, 220);

        // Lower cost first
        when(supplierPartRepository.findPreferredSuppliersByPartId("PART-BAT-001")).thenReturn(
                List.of(supplier1, supplier2));

        final SupplierSelectionResponse response = supplierSelectionService.selectBest("PART-BAT-001");

        assertEquals("SUP-001", response.selectedSupplierId());
    }

    @Test
    void selectBest_NoSuppliers_ThrowsException() {

        when(supplierPartRepository.findPreferredSuppliersByPartId("PART-BAT-001")).thenReturn(List.of());

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> supplierSelectionService.selectBest("PART-BAT-001"));

        assertEquals("No supplier available for part PART-BAT-001", exception.getMessage());
    }

    private SupplierPart createSupplierPart(final String supplierId, final int leadTime, final double rating,
            final double unitCost) {

        final Supplier supplier = new Supplier();
        supplier.setSupplierId(supplierId);
        supplier.setRating(BigDecimal.valueOf(rating));

        final SupplierPart supplierPart = new SupplierPart();
        supplierPart.setSupplier(supplier);
        supplierPart.setLeadTimeDays(leadTime);
        supplierPart.setUnitCost(BigDecimal.valueOf(unitCost));

        return supplierPart;
    }
}