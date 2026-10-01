package com.smartfactory.common.dto.procurement;

import java.math.BigDecimal;

public record SuppliersForPartResponse(String supplierId, String name, Integer leadTimeDays, BigDecimal rating,
                                       BigDecimal unitCost) {
}
