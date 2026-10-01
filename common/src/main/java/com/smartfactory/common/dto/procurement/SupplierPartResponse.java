package com.smartfactory.common.dto.procurement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SupplierPartResponse(Long supplierPartId, String supplierId, String partCode, BigDecimal unitCost,
                                   LocalDate createdDate) {
}