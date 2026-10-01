package com.smartfactory.common.dto.procurement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SupplierResponse(String supplierId, String name, Integer leadTimeDays, BigDecimal rating,
                               String contactEmail, Boolean active, LocalDate createdDate) {
}
