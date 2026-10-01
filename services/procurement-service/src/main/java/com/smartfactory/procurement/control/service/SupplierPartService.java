package com.smartfactory.procurement.control.service;

import java.util.List;

import com.smartfactory.common.dto.procurement.CreateSupplierPartRequest;
import com.smartfactory.common.dto.procurement.SupplierPartResponse;
import com.smartfactory.common.dto.procurement.SuppliersForPartResponse;

public interface SupplierPartService {
    public SupplierPartResponse createRelationship(final CreateSupplierPartRequest request);

    public List<SupplierPartResponse> getAllSupplierParts();

    public SupplierPartResponse getSupplierPartById(final Long id);

    public List<SuppliersForPartResponse> getSuppliersOrderedByLeadTimeAsc(final String partId);
}
