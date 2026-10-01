package com.smartfactory.procurement.control.service;

import java.util.List;

import com.smartfactory.common.dto.procurement.CreateSupplierRequest;
import com.smartfactory.common.dto.procurement.SupplierResponse;
import jakarta.validation.Valid;

public interface SupplierService {

    SupplierResponse createSupplier(@Valid final CreateSupplierRequest supplierRequest);

    List<SupplierResponse> getAllSuppliers();

    SupplierResponse getSupplierById(final String supplierId);
}
