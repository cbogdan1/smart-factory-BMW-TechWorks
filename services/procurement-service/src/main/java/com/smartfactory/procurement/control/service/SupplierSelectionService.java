package com.smartfactory.procurement.control.service;

import com.smartfactory.common.dto.procurement.SupplierSelectionResponse;

public interface SupplierSelectionService {

    SupplierSelectionResponse selectBest(String partId);
}
