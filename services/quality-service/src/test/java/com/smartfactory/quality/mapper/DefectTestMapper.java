package com.smartfactory.quality.mapper;

import java.time.LocalDateTime;

import com.smartfactory.common.dto.quality.CreateDefectRequest;
import com.smartfactory.common.dto.quality.DefectResponse;
import com.smartfactory.quality.entity.Defect;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DefectTestMapper {

    public CreateDefectRequest toCreateRequest(final String code) {
        return toCreateRequest(code, "", 0);
    }

    public CreateDefectRequest toCreateRequest(final String code, final String description,
            final Integer penaltyPoints) {

        final CreateDefectRequest request = new CreateDefectRequest();

        request.setCode(code);
        request.setDescription(description);
        request.setPenaltyPoints(penaltyPoints);

        return request;
    }

    public Defect toDefect(final String code) {
        return toDefect(code, "", 0);
    }

    public Defect toDefect(final String code, final String description, final Integer penaltyPoints) {

        final Defect defect = new Defect();

        defect.setDefectId("DEF-" + code);
        defect.setCode(code);
        defect.setDescription(description);
        defect.setPenaltyPoints(penaltyPoints);
        defect.setActive(true);
        defect.setCreatedDate(LocalDateTime.now());

        return defect;
    }

    public DefectResponse toResponse(final String code, final String description, final Integer penaltyPoints) {

        final DefectResponse response = new DefectResponse();

        response.setCode(code);
        response.setDescription(description);
        response.setPenaltyPoints(penaltyPoints);
        response.setActive(true);
        response.setCreatedDate(LocalDateTime.now());

        return response;
    }
}