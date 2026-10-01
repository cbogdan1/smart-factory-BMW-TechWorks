package com.smartfactory.quality.control.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import com.smartfactory.common.dto.quality.CreateDefectRequest;
import com.smartfactory.common.dto.quality.DefectResponse;
import com.smartfactory.quality.entity.Defect;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DefectMapper {

    public Defect toDefect(final CreateDefectRequest request) {

        final Defect defect = new Defect();
        defect.setDefectId(generateDefectId());
        defect.setCode(request.getCode());
        defect.setDescription(request.getDescription());
        defect.setPenaltyPoints(request.getPenaltyPoints());
        defect.setActive(true);
        defect.setCreatedDate(LocalDateTime.now());
        defect.setAffectedStage(request.getAffectedStage());

        return defect;
    }

    public DefectResponse toResponse(final Defect defect) {

        final DefectResponse response = new DefectResponse();

        response.setDefectId(defect.getDefectId());
        response.setCode(defect.getCode());
        response.setDescription(defect.getDescription());
        response.setPenaltyPoints(defect.getPenaltyPoints());
        response.setActive(defect.getActive());
        response.setCreatedDate(defect.getCreatedDate());
        response.setAffectedStage(defect.getAffectedStage());

        return response;
    }

    private String generateDefectId() {
        // Generate a unique defect ID
        return "DEF-" + UUID.randomUUID();
    }

}