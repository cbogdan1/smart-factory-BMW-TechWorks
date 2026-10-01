package com.smartfactory.procurement.control.service;

import java.time.LocalDateTime;

import com.smartfactory.common.dto.procurement.CreatePurchaseOrderRequest;
import com.smartfactory.common.dto.procurement.CreatePurchaseOrderResponse;
import com.smartfactory.common.dto.procurement.SupplierSelectionResponse;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.event.PartsShortageDetectedEvent;
import com.smartfactory.common.payloads.procurement.PartsShortageDetectedPayload;
import com.smartfactory.procurement.control.repository.ProcessedEventRepository;
import com.smartfactory.procurement.control.repository.PurchaseOrderRepository;
import com.smartfactory.procurement.entity.ProcessedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class ProcurementServiceImpl implements ProcurementService {
    @Inject
    PurchaseOrderService purchaseOrderService;
    @Inject
    ProcessedEventRepository processedEventRepository;
    @Inject
    SupplierSelectionService supplierSelectionService;
    @Inject
    SimulateDeliveryService simulateDeliveryService;
    @Inject
    PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public PartsDeliveredEvent createPurchaseOrder(final PartsShortageDetectedEvent event) {
        final String eventId = event.event().eventId();
        if (processedEventRepository.findByIdOptional(eventId).isPresent()) {
            log.info("Ignoring duplicate event {}", eventId);
            return null;
        }
        final PartsShortageDetectedPayload payload = event.event().payload();
        final SupplierSelectionResponse bestSupplier = supplierSelectionService.selectBest(payload.partId());
        final String supplierId = bestSupplier.selectedSupplierId();

        final CreatePurchaseOrderResponse purchaseOrderResponse = purchaseOrderService.createPurchaseOrder(
                new CreatePurchaseOrderRequest(payload.partId(), supplierId, payload.missingQuantity()));
        processedEventRepository.persist(
                new ProcessedEvent(event.event().eventId(), event.event().eventType(), LocalDateTime.now()));
        log.info("Created purchase order for partId={}, missingQuantity={}", payload.partId(),
                payload.missingQuantity());
        purchaseOrderService.markEventProcessed(purchaseOrderResponse.purchaseOrderId());
        return simulateDeliveryService.simulateDelivery(purchaseOrderResponse.purchaseOrderId(),
                event.event().payload().vehicleId());
    }
}
