package com.smartfactory.quality.control.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import com.smartfactory.common.dto.quality.AddDefectToInspectionRequest;
import com.smartfactory.common.dto.quality.InspectionDefectResponse;
import com.smartfactory.quality.entity.InspectionDefect;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InspectionDefectMapper {

    public InspectionDefect toInspectionDefect(final String inspectionId, final AddDefectToInspectionRequest request) {

        final InspectionDefect inspectionDefect = new InspectionDefect();
        inspectionDefect.setInspectionDefectId(generateInspectionDefectID());
        inspectionDefect.setInspectionId(inspectionId);
        inspectionDefect.setDefectCode(request.getDefectCode());
        inspectionDefect.setComment(request.getComment());
        inspectionDefect.setCreatedDate(LocalDateTime.now());

        return inspectionDefect;
    }

    public InspectionDefectResponse toResponse(final InspectionDefect inspectionDefect) {

        final InspectionDefectResponse response = new InspectionDefectResponse();
        response.setInspectionDefectId(inspectionDefect.getInspectionDefectId());
        response.setInspectionId(inspectionDefect.getInspectionId());
        response.setDefectCode(inspectionDefect.getDefectCode());
        response.setComment(inspectionDefect.getComment());
        response.setCreatedDate(inspectionDefect.getCreatedDate());

        return response;
    }

    private String generateInspectionDefectID() {
        return "INSPDEF-" + UUID.randomUUID();
    }
}
