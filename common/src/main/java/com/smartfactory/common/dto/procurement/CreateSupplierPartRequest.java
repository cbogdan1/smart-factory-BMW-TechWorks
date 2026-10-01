package com.smartfactory.common.dto.procurement;

import java.math.BigDecimal;

public record CreateSupplierPartRequest(String supplierId, String partId, BigDecimal unitCost, Integer leadTimeDays) {
}