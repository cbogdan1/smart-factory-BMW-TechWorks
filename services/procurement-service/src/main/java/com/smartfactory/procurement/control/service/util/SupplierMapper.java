package com.smartfactory.procurement.control.service.util;

import com.smartfactory.common.dto.procurement.SupplierResponse;
import com.smartfactory.procurement.entity.Supplier;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SupplierMapper {

    public static SupplierResponse mapToSupplierResponse(final Supplier supplier) {
        return new SupplierResponse(supplier.getSupplierId(), supplier.getName(), supplier.getLeadTimeDays(),
                supplier.getRating(), supplier.getContactEmail(), supplier.getActive(), supplier.getCreatedDate());
    }
}
