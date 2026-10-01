package com.smartfactory.procurement.control.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.smartfactory.common.dto.procurement.CreatePurchaseOrderRequest;
import com.smartfactory.common.dto.procurement.CreatePurchaseOrderResponse;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineRequest;
import com.smartfactory.common.dto.procurement.PurchaseOrderStateMachineResponse;
import com.smartfactory.common.enums.PurchaseOrderStatus;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.ProcurementServiceExceptions;
import com.smartfactory.procurement.control.repository.PurchaseOrderRepository;
import com.smartfactory.procurement.control.repository.PurchaseOrderStatusHistoryRepository;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.service.util.PurchaseOrderMapper;
import com.smartfactory.procurement.control.service.util.PurchaseOrderTransition;
import com.smartfactory.procurement.entity.PurchaseOrder;
import com.smartfactory.procurement.entity.PurchaseOrderStatusHistory;
import com.smartfactory.procurement.entity.SupplierPart;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    @Inject
    PurchaseOrderRepository purchaseOrderRepository;
    @Inject
    SupplierPartRepository supplierPartRepository;
    @Inject
    PurchaseOrderStatusHistoryRepository purchaseOrderStatusHistoryRepository;

    @Override
    @Transactional
    public CreatePurchaseOrderResponse createPurchaseOrder(final CreatePurchaseOrderRequest request) {

        final SupplierPart supplierPart =
                supplierPartRepository.findBySupplierIdAndPartId(request.supplierId(), request.partId()).orElseThrow(
                        () -> new BusinessException(
                                "Supplier with id " + request.supplierId() + " does not provide part with id "
                                        + request.partId(), ProcurementServiceExceptions.SUPPLIER_PART_NOT_FOUND, 404));
        final PurchaseOrder purchaseOrder = new PurchaseOrder();
        final String uniqueId = generateUniqueId();
        purchaseOrder.setPurchaseOrderId(uniqueId);
        purchaseOrder.setOrderDate(LocalDateTime.now());
        purchaseOrder.setQuantity(request.quantity());
        purchaseOrder.setStatus(PurchaseOrderStatus.CREATED);
        purchaseOrder.setPartId(request.partId());
        purchaseOrder.setSupplierId(request.supplierId());
        purchaseOrder.setDeliveryDate(LocalDateTime.now().plusDays(supplierPart.getSupplier().getLeadTimeDays()));
        purchaseOrder.setTotalPrice(supplierPart.getUnitCost().multiply(BigDecimal.valueOf(request.quantity())));

        purchaseOrderRepository.persist(purchaseOrder);
        return PurchaseOrderMapper.mapToPurchaseOrderResponse(purchaseOrder);

    }

    @Override
    @Transactional
    public PurchaseOrderStateMachineResponse changeStatus(final String purchaseOrderId,
            final PurchaseOrderStateMachineRequest request) {
        final PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPurchaseOrderId(purchaseOrderId).orElseThrow(
                () -> new BusinessException("Purchase Order with id " + purchaseOrderId + " does not exist.",
                        ProcurementServiceExceptions.PURCHASE_ORDER_NOT_FOUND, 404));
        if (!PurchaseOrderTransition.isValidTransition(purchaseOrder.getStatus(), request.newStatus())) {
            throw new BusinessException(
                    "Invalid purchase order status transition from " + purchaseOrder.getStatus() + " to "
                            + request.newStatus(),
                    ProcurementServiceExceptions.PURCHASE_ORDER_INVALID_STATUS_TRANSITION, 409);
        }
        final PurchaseOrderStatusHistory purchaseOrderStatusHistory = new PurchaseOrderStatusHistory();
        purchaseOrderStatusHistory.setNewStatus(request.newStatus());
        purchaseOrderStatusHistory.setPurchaseOrderId(purchaseOrderId);
        purchaseOrderStatusHistory.setLastModifiedDate(LocalDateTime.now());
        purchaseOrderStatusHistory.setOldStatus(purchaseOrder.getStatus());

        purchaseOrderStatusHistoryRepository.persist(purchaseOrderStatusHistory);

        purchaseOrder.setStatus(request.newStatus());
        purchaseOrderRepository.persist(purchaseOrder);
        return PurchaseOrderMapper.mapToPurchaseOrderStateMachineResponse(purchaseOrderStatusHistory, purchaseOrder);
    }

    public void markEventProcessed(final String purchaseOrderId) {
        changeStatus(purchaseOrderId, new PurchaseOrderStateMachineRequest(PurchaseOrderStatus.SENT));
        changeStatus(purchaseOrderId, new PurchaseOrderStateMachineRequest(PurchaseOrderStatus.CONFIRMED));
    }

    private String generateUniqueId() {
        final long supplierCount = purchaseOrderRepository.count();
        return String.format("PO-%03d", supplierCount + 1);
    }
}
