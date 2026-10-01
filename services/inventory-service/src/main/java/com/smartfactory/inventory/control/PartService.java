package com.smartfactory.inventory.control;

import java.util.List;
import java.util.UUID;
import com.smartfactory.common.dto.inventory.CreatePartRequest;
import com.smartfactory.common.dto.inventory.PartResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.inventory.control.exception.PartAlreadyExistsException;
import com.smartfactory.inventory.control.exception.PartNotFoundException;
import com.smartfactory.inventory.control.mapper.PartMapper;
import com.smartfactory.inventory.entity.Part;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PartService {
    @Inject
    PartRepository partRepository;

    @Inject
    PartMapper partMapper;

    /**
     * Creates a new part in the inventory system.
     *
     * @param request The request object containing the part details.
     * @return The response object containing the created part details.
     * @throws BusinessException if a part with the same code already exists.
     */
    @Transactional
    public PartResponse createPart(final CreatePartRequest request) {
        log.info("Creating new part with code: {}", request.getPartCode());
        partRepository.findByPartCode(request.getPartCode()).ifPresent(part -> {
            throw new PartAlreadyExistsException(request.getPartCode());
        });
        final Part part = partMapper.toEntity(request);
        partRepository.persist(part);
        log.info("Successfully created part with ID: {}", part.getId());
        return partMapper.toResponse(part);
    }

    /**
     * Retrieves a list of all parts in the inventory system.
     *
     * @return A list of response objects containing the part details.
     */
    public List<PartResponse> getAllParts() {
        return partRepository.listAll().stream().map(partMapper::toResponse).toList();
    }

    /**
     * Retrieves a part by its ID from the inventory system.
     *
     * @param id The ID of the part to retrieve.
     * @return The response object containing the part details.
     * @throws BusinessException if a part with the specified ID is not found.
     */
    public PartResponse getPartById(final UUID id) {
        final Part part = partRepository.findByIdOptional(id)
                .orElseThrow(() -> new PartNotFoundException(id));
        return partMapper.toResponse(part);
    }

    /**
     * Retrieves a part by its part code from the inventory system.
     *
     * @param partCode The part code of the part to retrieve.
     * @return The response object containing the part details.
     * @throws BusinessException if a part with the specified code is not found.
     */
    public PartResponse getPartByCode(final String partCode) {
        final Part part = partRepository.findByPartCode(partCode)
                .orElseThrow(() -> new PartNotFoundException(partCode));
        return partMapper.toResponse(part);
    }

    public Part getPartEntityByCode(final String partCode) {
        return partRepository.findByPartCode(partCode)
                .orElseThrow(() -> new PartNotFoundException(partCode));
    }

    public PartResponse getPartByPartId(final String partId) {
        final Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new PartNotFoundException(partId));
        return partMapper.toResponse(part);
    }
}
