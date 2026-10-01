package com.smartfactory.procurement.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.smartfactory.common.Topics;
import com.smartfactory.common.enums.PurchaseOrderStatus;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.ProcurementServiceExceptions;
import com.smartfactory.procurement.control.repository.PurchaseOrderRepository;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.service.SimulateDeliveryServiceImpl;
import com.smartfactory.procurement.entity.PurchaseOrder;
import com.smartfactory.procurement.entity.SupplierPart;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class SimulateDeliveryServiceTest {

    @Inject
    SimulateDeliveryServiceImpl simulateDeliveryService;

    @InjectMock
    PurchaseOrderRepository purchaseOrderRepository;

    @InjectMock
    SupplierPartRepository supplierPartRepository;

    @Test
    void simulateDelivery_Success_ReturnsEventAndUpdatesStatus() {

        final PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderId("PO-001");
        purchaseOrder.setSupplierId("SUP-001");
        purchaseOrder.setPartId("PART-BAT-001");
        purchaseOrder.setQuantity(5);
        purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);

        final SupplierPart supplierPart = new SupplierPart();
        supplierPart.setPartId("PART-BAT-001");

        when(purchaseOrderRepository.findByPurchaseOrderId("PO-001")).thenReturn(Optional.of(purchaseOrder));

        when(supplierPartRepository.findBySupplierIdAndPartId("SUP-001", "PART-BAT-001")).thenReturn(
                Optional.of(supplierPart));

        final PartsDeliveredEvent event = simulateDeliveryService.simulateDelivery("PO-001", "BMW-001");

        assertNotNull(event);

        assertEquals(PurchaseOrderStatus.DELIVERED, purchaseOrder.getStatus());

        assertEquals("PO-001", event.event().payload().purchaseOrderId());

        assertEquals("SUP-001", event.event().payload().supplierId());

        assertEquals("PART-BAT-001", event.event().payload().partId());

        assertEquals("PART-BAT-001", event.event().payload().partCode());

        assertEquals(5, event.event().payload().quantity());

        assertEquals("BMW-001", event.event().correlationId());

        assertEquals(Topics.PARTS_DELIVERED, event.event().eventType());

        assertTrue(event.event().eventId().startsWith("EVT-"));
    }

    @Test
    void simulateDelivery_PurchaseOrderNotFound_ThrowsException() {

        when(purchaseOrderRepository.findByPurchaseOrderId("PO-999")).thenReturn(Optional.empty());

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> simulateDeliveryService.simulateDelivery("PO-999", "BMW-001"));

        assertEquals("Purchase Order id: PO-999 not found.", exception.getMessage());
    }

    @Test
    void simulateDelivery_SupplierPartMissing_ThrowsNotFound() {

        final PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderId("PO-001");
        purchaseOrder.setSupplierId("SUP-001");
        purchaseOrder.setPartId("PART-BAT-001");
        purchaseOrder.setQuantity(5);
        purchaseOrder.setStatus(PurchaseOrderStatus.CONFIRMED);

        when(purchaseOrderRepository.findByPurchaseOrderId("PO-001")).thenReturn(Optional.of(purchaseOrder));

        when(supplierPartRepository.findBySupplierIdAndPartId("SUP-001", "PART-BAT-001")).thenReturn(Optional.empty());

        final BusinessException exception = assertThrows(BusinessException.class,
                () -> simulateDeliveryService.simulateDelivery("PO-001", "BMW-001"));

        assertEquals(ProcurementServiceExceptions.SUPPLIER_PART_NOT_FOUND, exception.getErrorCode());
    }
}