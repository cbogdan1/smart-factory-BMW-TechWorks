package com.smartfactory.procurement.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import com.smartfactory.common.dto.procurement.CreatePurchaseOrderResponse;
import com.smartfactory.common.dto.procurement.SupplierSelectionResponse;
import com.smartfactory.common.enums.PurchaseOrderStatus;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.event.PartsShortageDetectedEvent;
import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;
import com.smartfactory.common.payloads.procurement.PartsShortageDetectedPayload;
import com.smartfactory.procurement.control.repository.ProcessedEventRepository;
import com.smartfactory.procurement.control.repository.PurchaseOrderRepository;
import com.smartfactory.procurement.control.service.ProcurementServiceImpl;
import com.smartfactory.procurement.control.service.PurchaseOrderService;
import com.smartfactory.procurement.control.service.SimulateDeliveryService;
import com.smartfactory.procurement.control.service.SupplierSelectionService;
import com.smartfactory.procurement.entity.ProcessedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceImplTest {

    @Mock
    PurchaseOrderService purchaseOrderService;

    @Mock
    ProcessedEventRepository processedEventRepository;

    @Mock
    SupplierSelectionService supplierSelectionService;

    @Mock
    SimulateDeliveryService simulateDeliveryService;

    @Mock
    PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    ProcurementServiceImpl procurementService;

    @Test
    void shouldIgnoreDuplicateEvent() {

        PartsShortageDetectedPayload payload =
                new PartsShortageDetectedPayload("VH-001", "PLAN-001", "PART-001", "Battery", 10, 4, 6);

        PartsShortageDetectedEvent event = new PartsShortageDetectedEvent(
                new DomainEvent<>("EVT-123", "PARTS_SHORTAGE_DETECTED", "procurement-service", "VH-001", payload));

        when(processedEventRepository.findByIdOptional("EVT-123")).thenReturn(Optional.of(mock(ProcessedEvent.class)));

        PartsDeliveredEvent result = procurementService.createPurchaseOrder(event);

        assertNull(result);

        verify(processedEventRepository).findByIdOptional("EVT-123");

        verifyNoInteractions(supplierSelectionService, purchaseOrderService, simulateDeliveryService);
    }

    @Test
    void shouldCreatePurchaseOrderAndSimulateDelivery() {

        PartsShortageDetectedPayload payload =
                new PartsShortageDetectedPayload("VH-001", "PLAN-001", "PART-001", "Battery", 10, 4, 6);

        PartsShortageDetectedEvent event = new PartsShortageDetectedEvent(
                new DomainEvent<>("EVT-123", "PARTS_SHORTAGE_DETECTED", "procurement-service", "VH-001", payload));

        when(processedEventRepository.findByIdOptional("EVT-123")).thenReturn(Optional.empty());

        SupplierSelectionResponse supplierResponse =
                new SupplierSelectionResponse("PART-001", "SUPPLIER-001", 2, BigDecimal.valueOf(4.8),
                        BigDecimal.valueOf(15.5));

        when(supplierSelectionService.selectBest("PART-001")).thenReturn(supplierResponse);

        CreatePurchaseOrderResponse poResponse =
                new CreatePurchaseOrderResponse("PO-001", "PART-001", "SUPPLIER-001", 6, PurchaseOrderStatus.CREATED,
                        LocalDateTime.now());

        when(purchaseOrderService.createPurchaseOrder(any())).thenReturn(poResponse);

        PartsDeliveredPayload deliveredPayload =
                new PartsDeliveredPayload("PO-001", "SUPPLIER-001", "PART-001", "PART-001", 6, Instant.now());

        PartsDeliveredEvent deliveredEvent = new PartsDeliveredEvent(
                new DomainEvent<>("EVT-999", "PARTS_DELIVERED", "procurement-service", "VH-001", deliveredPayload));

        when(simulateDeliveryService.simulateDelivery("PO-001", "VH-001")).thenReturn(deliveredEvent);

        PartsDeliveredEvent result = procurementService.createPurchaseOrder(event);

        assertNotNull(result);
        assertEquals(deliveredEvent, result);

        verify(supplierSelectionService).selectBest("PART-001");

        verify(purchaseOrderService).createPurchaseOrder(any());

        verify(processedEventRepository).persist(any(ProcessedEvent.class));
    }
}