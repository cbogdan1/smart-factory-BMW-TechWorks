package com.smartfactory.inventory.control.mapper;

import com.smartfactory.common.dto.inventory.CreatePartRequest;
import com.smartfactory.common.dto.inventory.PartResponse;
import com.smartfactory.inventory.entity.Part;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PartMapper {
    public Part toEntity(final CreatePartRequest request) {
        if (request == null) {
            return null;
        }
        final Part part = new Part();
        part.setPartId(request.getPartId());
        part.setPartCode(request.getPartCode());
        part.setPartName(request.getPartName());
        part.setDescription(request.getDescription());
        part.setUnitOfMeasure(request.getUnitOfMeasure());
        part.setActive(request.getActive() != null ? request.getActive() : true);
        return part;
    }

    public PartResponse toResponse(final Part part) {
        if (part == null) {
            return null;
        }
        return new PartResponse(part.getId(), part.getPartId(), part.getPartCode(), part.getPartName(), part.getDescription(), part.getUnitOfMeasure(), part.getActive(), part.getCreatedDate());
    }
}
