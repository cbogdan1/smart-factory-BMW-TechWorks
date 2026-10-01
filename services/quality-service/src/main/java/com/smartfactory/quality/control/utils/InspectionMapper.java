package com.smartfactory.quality.control.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import com.smartfactory.common.dto.quality.CreateInspectionRequest;
import com.smartfactory.common.dto.quality.InspectionDecisionResponse;
import com.smartfactory.common.dto.quality.InspectionResponse;
import com.smartfactory.common.dto.quality.InspectionScoreResponse;
import com.smartfactory.common.enums.InspectionStatus;
import com.smartfactory.quality.entity.Inspection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InspectionMapper {

    public Inspection toInspection(final CreateInspectionRequest request) {
        final Inspection inspection = new Inspection();
        inspection.setInspectionId(generateInspectionID());
        inspection.setVehicleId(request.getVehicleId());
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspection.setInspector(request.getInspector());

        return inspection;
    }

    public InspectionResponse toResponse(final Inspection inspection) {
        final InspectionResponse response = new InspectionResponse();
        response.setInspectionId(inspection.getInspectionId());
        response.setVehicleId(inspection.getVehicleId());
        response.setInspectionDate(inspection.getInspectionDate());
        response.setStatus(inspection.getStatus());
        response.setInspector(inspection.getInspector());

        return response;
    }

    public InspectionScoreResponse toScoreResponse(final Inspection inspection, final int defectsCount) {
        final InspectionScoreResponse response = new InspectionScoreResponse();
        response.setInspectionId(inspection.getInspectionId());
        response.setVehicleId(inspection.getVehicleId());
        response.setScore(inspection.getScore());
        response.setDefectsCount(defectsCount);
        response.setCalculatedAt(inspection.getCalculatedAt());
        response.setDecision(inspection.getDecision());
        response.setTargetStage(inspection.getTargetStage());

        return response;
    }

    public InspectionDecisionResponse toDecisionResponse(final Inspection inspection) {
        final InspectionDecisionResponse response = new InspectionDecisionResponse();
        response.setInspectionId(inspection.getInspectionId());
        response.setDecision(inspection.getDecision());
        response.setTargetStage(inspection.getTargetStage());

        return response;
    }

    private String generateInspectionID() {
        return "INSP-" + UUID.randomUUID();
    }
}
