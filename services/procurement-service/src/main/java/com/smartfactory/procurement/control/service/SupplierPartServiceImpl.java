package com.smartfactory.procurement.control.service;

import java.time.LocalDate;
import java.util.List;

import com.smartfactory.common.dto.procurement.CreateSupplierPartRequest;
import com.smartfactory.common.dto.procurement.SupplierPartResponse;
import com.smartfactory.common.dto.procurement.SuppliersForPartResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.exception.ProcurementServiceExceptions;
import com.smartfactory.procurement.control.InventoryClient;
import com.smartfactory.procurement.control.repository.SupplierPartRepository;
import com.smartfactory.procurement.control.repository.SupplierRepository;
import com.smartfactory.procurement.control.service.util.SupplierPartMapper;
import com.smartfactory.procurement.entity.SupplierPart;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class SupplierPartServiceImpl implements SupplierPartService {

    @Inject
    SupplierPartRepository supplierPartRepository;

    @Inject
    SupplierRepository supplierRepository;

    @Inject
    @RestClient
    InventoryClient inventoryClient;

    @Override
    @Transactional
    public SupplierPartResponse createRelationship(final CreateSupplierPartRequest request) {

        final var supplier = supplierRepository.find("supplierId", request.supplierId()).firstResultOptional()
                .orElseThrow(() -> new BusinessException("Supplier with id " + request.supplierId() + " not found.",
                        ProcurementServiceExceptions.SUPPLIER_NOT_FOUND, 404));

        try {
            inventoryClient.getPartById(request.partId());
        } catch (final WebApplicationException e) {
            if (e.getResponse() != null && e.getResponse().getStatus() == 404) {
                throw new BusinessException("Part with id " + request.partId() + " not found",
                        ProcurementServiceExceptions.PART_NOT_FOUND, 404);
            }
            throw e;
        }

        if (supplierPartRepository.existsBySupplierAndPart(request.supplierId(), request.partId())) {
            throw new BusinessException("Supplier already provides part with id " + request.partId(),
                    ProcurementServiceExceptions.SUPPLIER_PART_ALREADY_EXISTS, 409);
        }

        final SupplierPart entity = new SupplierPart();
        entity.setSupplier(supplier);
        entity.setPartId(request.partId());
        entity.setUnitCost(request.unitCost());
        entity.setCreatedDate(LocalDate.now());
        entity.setLeadTimeDays(request.leadTimeDays());

        supplierPartRepository.persist(entity);

        return SupplierPartMapper.mapToSupplierPart(entity);
    }

    @Override
    public List<SupplierPartResponse> getAllSupplierParts() {
        return supplierPartRepository.listAll().stream().map(SupplierPartMapper::mapToSupplierPart).toList();
    }

    @Override
    public SupplierPartResponse getSupplierPartById(final Long id) {
        return supplierPartRepository.findByIdOptional(id).map(SupplierPartMapper::mapToSupplierPart).orElseThrow(
                () -> new BusinessException("Supplier part id " + id + " not found",
                        ProcurementServiceExceptions.SUPPLIER_PART_NOT_FOUND, 404));
    }

    @Override
    public List<SuppliersForPartResponse> getSuppliersOrderedByLeadTimeAsc(final String partId) {
        try {
            inventoryClient.getPartById(partId);
        } catch (final WebApplicationException e) {
            if (e.getResponse() != null && e.getResponse().getStatus() == 404) {
                throw new BusinessException("Part id " + partId + " not found",
                        ProcurementServiceExceptions.PART_NOT_FOUND, 404);
            }
            throw e;
        }

        return supplierPartRepository.findPreferredSuppliersByPartId(partId).stream()
                .map(SupplierPartMapper::mapToSupplierForPartResponse).toList();
    }

}