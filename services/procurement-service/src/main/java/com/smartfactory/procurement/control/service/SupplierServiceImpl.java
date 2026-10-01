package com.smartfactory.procurement.control.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.smartfactory.common.dto.procurement.CreateSupplierRequest;
import com.smartfactory.common.dto.procurement.SupplierResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.ProcurementServiceExceptions;
import com.smartfactory.procurement.control.repository.SupplierRepository;
import com.smartfactory.procurement.control.service.util.SupplierMapper;
import com.smartfactory.procurement.entity.Supplier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@ApplicationScoped
public class SupplierServiceImpl implements SupplierService {

    @Inject
    SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierResponse createSupplier(@Valid final CreateSupplierRequest supplierRequest) {
        validateSupplier(supplierRequest);

        final Supplier supplier = new Supplier();
        final String generateUniqueId = generateUniqueId();

        supplier.setSupplierId(generateUniqueId);
        supplier.setName(supplierRequest.name());
        supplier.setLeadTimeDays(supplierRequest.leadTimeDays());
        supplier.setRating(supplierRequest.rating());
        supplier.setContactEmail(supplierRequest.contactEmail());
        supplier.setActive(supplierRequest.active());

        supplierRepository.persist(supplier);

        return SupplierMapper.mapToSupplierResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.listAll().stream().map(SupplierMapper::mapToSupplierResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse getSupplierById(final String supplierId) {
        final Supplier supplier = supplierRepository.findBySupplierId(supplierId).orElseThrow(
                () -> new BusinessException("Supplier with id " + supplierId + " not found.",
                        ProcurementServiceExceptions.SUPPLIER_NOT_FOUND, 404));

        return SupplierMapper.mapToSupplierResponse(supplier);
    }

    private String generateUniqueId() {
        final long supplierCount = supplierRepository.count();
        return String.format("SUP-%03d", supplierCount + 1);
    }

    private boolean compareRating(final BigDecimal rating) {
        final BigDecimal minValue = BigDecimal.ZERO;
        final BigDecimal maxValue = new BigDecimal("5.0");

        return rating.compareTo(minValue) < 0 || rating.compareTo(maxValue) > 0;
    }

    private void validateSupplier(final CreateSupplierRequest supplierRequest) {
        if (supplierRepository.findByName(supplierRequest.name()).isPresent()) {
            throw new BusinessException("A supplier with this name already exists.",
                    ProcurementServiceExceptions.SUPPLIER_NAME_EXISTS, 409);
        }

        if (supplierRepository.findByContactEmail(supplierRequest.contactEmail()).isPresent()) {
            throw new BusinessException("A supplier with this email already exists.",
                    ProcurementServiceExceptions.SUPPLIER_EMAIL_EXISTS, 409);
        }
        if (supplierRequest.leadTimeDays() < 0) {
            throw new BusinessException("A supplier cannot have negative lead time days",
                    ProcurementServiceExceptions.SUPPLIER_INVALID_LEAD_TIME, 400);
        }

        if (compareRating(supplierRequest.rating())) {
            throw new BusinessException("A supplier's rating must be between 0 and 5.",
                    ProcurementServiceExceptions.SUPPLIER_INVALID_RATING, 400);
        }
    }
}