package com.smartfactory.common.dto.procurement;

import com.smartfactory.common.enums.PurchaseOrderStatus;

public record PurchaseOrderStateMachineRequest(PurchaseOrderStatus newStatus) {
}
