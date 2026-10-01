package com.smartfactory.common.dto.quality;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InspectionDefectResponse {

    private String inspectionDefectId;
    private String inspectionId;
    private String defectCode;
    private String comment;
    private LocalDateTime createdDate;
}
