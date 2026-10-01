package com.smartfactory.procurement.control.service;

import java.util.List;

import com.smartfactory.common.dto.procurement.SupplierSelectionResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.ProcurementServiceExceptions;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.service.util.SupplierSelectionMapper;
import com.smartfactory.procurement.entity.SupplierPart;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SupplierSelectionServiceImpl implements SupplierSelectionService {

    @Inject
    SupplierPartRepository supplierPartRepository;

    @Override
    @Transactional
    public SupplierSelectionResponse selectBest(final String partId) {

        final List<SupplierPart> candidates = supplierPartRepository.findPreferredSuppliersByPartId(partId);

        if (candidates.isEmpty()) {
            throw new BusinessException("No supplier available for part " + partId,
                    ProcurementServiceExceptions.NO_SUPPLIER_AVAILABLE, 422);
        }

        final SupplierPart bestSupplierPart = candidates.getFirst();

        return SupplierSelectionMapper.mapToSupplierSelectionResponse(bestSupplierPart);
    }
}
