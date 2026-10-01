package com.smartfactory.common.payloads.procurement;

import java.time.Instant;

public record PartsDeliveredPayload(String purchaseOrderId, String supplierId, String partId, String partCode,
                                    int quantity, Instant deliveredAt) {
}