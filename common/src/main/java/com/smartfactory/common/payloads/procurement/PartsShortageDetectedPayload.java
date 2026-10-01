package com.smartfactory.common.payloads.procurement;

public record PartsShortageDetectedPayload(String vehicleId, String planId, String partId, String partName,
                                           int requiredQuantity, int availableQuantity, int missingQuantity) {
}
