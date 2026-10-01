package com.smartfactory.procurement.control.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.smartfactory.common.Topics;
import com.smartfactory.common.enums.PurchaseOrderStatus;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.event.PartsDeliveredEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.ProcurementServiceExceptions;
import com.smartfactory.common.payloads.procurement.PartsDeliveredPayload;
import com.smartfactory.procurement.control.repository.PurchaseOrderRepository;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.service.util.PurchaseOrderTransition;
import com.smartfactory.procurement.entity.PurchaseOrder;
import com.smartfactory.procurement.entity.SupplierPart;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SimulateDeliveryServiceImpl implements SimulateDeliveryService {

    private static final String SOURCE_SYSTEM = "procurement-service";
    @Inject
    PurchaseOrderRepository purchaseOrderRepository;
    @Inject
    SupplierPartRepository supplierPartRepository;

    @Override
    @Transactional
    public PartsDeliveredEvent simulateDelivery(final String purchaseOrderId, final String vehicleId) {

        final PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId).orElseThrow(
                () -> new BusinessException("Purchase Order id: " + purchaseOrderId + " not found.",
                        ProcurementServiceExceptions.SUPPLIER_PART_NOT_FOUND, 404));

        if (!PurchaseOrderTransition.isValidTransition(purchaseOrder.getStatus(), PurchaseOrderStatus.DELIVERED)) {
            throw new BusinessException(
                    "Invalid purchase order status transition from " + purchaseOrder.getStatus() + " to "
                            + PurchaseOrderStatus.DELIVERED,
                    ProcurementServiceExceptions.PURCHASE_ORDER_INVALID_STATUS_TRANSITION, 409);
        }

        purchaseOrder.setStatus(PurchaseOrderStatus.DELIVERED);

        final String partCode = supplierPartRepository.findBySupplierIdAndPartId(purchaseOrder.getSupplierId(),
                purchaseOrder.getPartId()).map(SupplierPart::getPartId).orElseThrow(() -> new BusinessException(
                "Supplier-part relationship not found for purchase order " + purchaseOrderId,
                ProcurementServiceExceptions.SUPPLIER_PART_NOT_FOUND, 404));

        final String eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        final PartsDeliveredPayload payload =
                new PartsDeliveredPayload(purchaseOrder.getPurchaseOrderId(), purchaseOrder.getSupplierId(),
                        purchaseOrder.getPartId(), partCode, purchaseOrder.getQuantity(),
                        Instant.now().truncatedTo(ChronoUnit.SECONDS));

        final DomainEvent<PartsDeliveredPayload> domainEvent =
                new DomainEvent<>(eventId, Topics.PARTS_DELIVERED, SOURCE_SYSTEM, vehicleId, payload);

        return new PartsDeliveredEvent(domainEvent);
    }
}
