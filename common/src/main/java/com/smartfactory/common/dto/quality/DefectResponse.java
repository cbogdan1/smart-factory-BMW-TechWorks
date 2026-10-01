package com.smartfactory.common.dto.quality;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.ProductionStage;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DefectResponse {

    private String defectId; //ex: DEF-001
    private String code; // ex: PAINT_SCRATCH
    private String description; // EX: paint scratch detected
    private Integer penaltyPoints;
    private Boolean active; // if defect active
    private LocalDateTime createdDate;
    private ProductionStage affectedStage;

}