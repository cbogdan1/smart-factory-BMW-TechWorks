package com.smartfactory.procurement.control.service.util;

import com.smartfactory.common.dto.procurement.SupplierPartResponse;
import com.smartfactory.common.dto.procurement.SuppliersForPartResponse;
import com.smartfactory.procurement.entity.SupplierPart;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SupplierPartMapper {
    public SupplierPartResponse mapToSupplierPart(final SupplierPart entity) {
        return new SupplierPartResponse(entity.getSupplierPartId(), entity.getSupplier().getSupplierId(),
                entity.getPartId(), entity.getUnitCost(), entity.getCreatedDate());
    }

    public SuppliersForPartResponse mapToSupplierForPartResponse(final SupplierPart entity) {
        return new SuppliersForPartResponse(entity.getSupplier().getSupplierId(), entity.getSupplier().getName(),
                entity.getLeadTimeDays(), entity.getSupplier().getRating(), entity.getUnitCost());
    }
}
