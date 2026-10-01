package com.smartfactory.procurement.control.service.util;

import com.smartfactory.common.enums.PurchaseOrderStatus;

public class PurchaseOrderTransition {
    public static boolean isValidTransition(final PurchaseOrderStatus currentStatus,
            final PurchaseOrderStatus newStatus) {

        if (currentStatus == PurchaseOrderStatus.CREATED) {
            return newStatus == PurchaseOrderStatus.SENT || newStatus == PurchaseOrderStatus.CANCELLED;
        }

        if (currentStatus == PurchaseOrderStatus.SENT) {
            return newStatus == PurchaseOrderStatus.CONFIRMED || newStatus == PurchaseOrderStatus.CANCELLED;
        }

        if (currentStatus == PurchaseOrderStatus.CONFIRMED) {
            return newStatus == PurchaseOrderStatus.DELIVERED || newStatus == PurchaseOrderStatus.CANCELLED;
        }

        return false;
    }
}
