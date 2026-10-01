package com.smartfactory.procurement.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import com.smartfactory.common.dto.procurement.CreatePurchaseOrderRequest;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineRequest;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineResponse;
import com.smartfactory.common.enums.PurchaseOrderStatus;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.procurement.control.repository.PurchaseOrderRepository;
import com.smartfactory.procurement.control.repository.PurchaseOrderStatusHistoryRepository;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.service.PurchaseOrderServiceImpl;
import com.smartfactory.procurement.entity.PurchaseOrder;
import com.smartfactory.procurement.entity.PurchaseOrderStatusHistory;
import com.smartfactory.procurement.entity.Supplier;
import com.smartfactory.procurement.entity.SupplierPart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    SupplierPartRepository supplierPartRepository;

    @InjectMocks
    PurchaseOrderServiceImpl purchaseOrderService;

    @Mock
    PurchaseOrderStatusHistoryRepository purchaseOrderStatusHistoryRepository;

    @Test
    void shouldCreatePurchaseOrder() {

        final CreatePurchaseOrderRequest request = new CreatePurchaseOrderRequest("PART-BAT-001", "SUP-001", 5);

        final Supplier supplier = new Supplier();
        supplier.setLeadTimeDays(7);

        final SupplierPart supplierPart = new SupplierPart();
        supplierPart.setSupplier(supplier);
        supplierPart.setUnitCost(new BigDecimal("100"));

        when(supplierPartRepository.findBySupplierIdAndPartId("SUP-001", "PART-BAT-001")).thenReturn(
                Optional.of(supplierPart));

        when(purchaseOrderRepository.count()).thenReturn(0L);

        purchaseOrderService.createPurchaseOrder(request);

        final ArgumentCaptor<PurchaseOrder> captor = ArgumentCaptor.forClass(PurchaseOrder.class);

        verify(purchaseOrderRepository).persist(captor.capture());

        final PurchaseOrder savedOrder = captor.getValue();

        assertEquals("PO-001", savedOrder.getPurchaseOrderId());
        assertEquals("PART-BAT-001", savedOrder.getPartId());
        assertEquals("SUP-001", savedOrder.getSupplierId());
        assertEquals(5, savedOrder.getQuantity());
        assertEquals(PurchaseOrderStatus.CREATED, savedOrder.getStatus());
        assertEquals(new BigDecimal("500"), savedOrder.getTotalPrice());
    }

    @Test
    void shouldThrowExceptionWhenSupplierPartIsMissing() {

        final CreatePurchaseOrderRequest request = new CreatePurchaseOrderRequest("PART-BAT-001", "SUP-001", 5);

        when(supplierPartRepository.findBySupplierIdAndPartId("SUP-001", "PART-BAT-001")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> purchaseOrderService.createPurchaseOrder(request));
    }

    @Test
    void shouldChangePurchaseOrderStatus() {

        final PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderId("PO-001");
        purchaseOrder.setSupplierId("SUP-001");
        purchaseOrder.setQuantity(5);
        purchaseOrder.setStatus(PurchaseOrderStatus.CREATED);

        final PurchaseOrderStateMachineRequest request = new PurchaseOrderStateMachineRequest(PurchaseOrderStatus.SENT);

        when(purchaseOrderRepository.findByPurchaseOrderId("PO-001")).thenReturn(Optional.of(purchaseOrder));

        final PurchaseOrderStateMachineResponse response = purchaseOrderService.changeStatus("PO-001", request);

        final ArgumentCaptor<PurchaseOrderStatusHistory> historyCaptor =
                ArgumentCaptor.forClass(PurchaseOrderStatusHistory.class);

        verify(purchaseOrderStatusHistoryRepository).persist(historyCaptor.capture());

        final PurchaseOrderStatusHistory history = historyCaptor.getValue();

        assertEquals(PurchaseOrderStatus.CREATED, history.getOldStatus());
        assertEquals(PurchaseOrderStatus.SENT, history.getNewStatus());
        assertEquals("PO-001", history.getPurchaseOrderId());

        assertEquals(PurchaseOrderStatus.SENT, purchaseOrder.getStatus());
        assertEquals(PurchaseOrderStatus.SENT, response.status());
    }
}