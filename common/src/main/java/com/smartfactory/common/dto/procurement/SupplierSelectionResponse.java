package com.smartfactory.common.dto.procurement;

import java.math.BigDecimal;

public record SupplierSelectionResponse(String partId, String selectedSupplierId, Integer leadTimeDays,
                                        BigDecimal rating, BigDecimal unitCost) {
}
