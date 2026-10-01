package com.smartfactory.procurement.control.service.util;

import com.smartfactory.common.dto.procurement.SupplierSelectionResponse;
import com.smartfactory.procurement.entity.SupplierPart;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SupplierSelectionMapper {

    public static SupplierSelectionResponse mapToSupplierSelectionResponse(final SupplierPart bestSupplierPart) {
        return new SupplierSelectionResponse(bestSupplierPart.getPartId(),
                bestSupplierPart.getSupplier().getSupplierId(), bestSupplierPart.getLeadTimeDays(),
                bestSupplierPart.getSupplier().getRating(), bestSupplierPart.getUnitCost());
    }
}
