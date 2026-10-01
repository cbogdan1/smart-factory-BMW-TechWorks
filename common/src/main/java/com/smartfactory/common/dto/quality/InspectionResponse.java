package com.smartfactory.common.dto.quality;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.InspectionStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InspectionResponse {

    private String inspectionId;
    private String vehicleId;
    private LocalDateTime inspectionDate;
    private InspectionStatus status;
    private String inspector;

}
