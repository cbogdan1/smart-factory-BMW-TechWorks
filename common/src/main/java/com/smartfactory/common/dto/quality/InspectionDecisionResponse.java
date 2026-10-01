package com.smartfactory.common.dto.quality;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.ProductionStage;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@Schema(name = "InspectionDecisionResponse", description = "Automatic decision taken for an inspection.")
public class InspectionDecisionResponse {

    @Schema(description = "Unique identifier of an inspection")
    private String inspectionId;

    @Schema(description = "Decision derived from the quality score. Null if no decision has been made.")
    private Decision decision;

    @Schema(description = "For a REWORK decision, the earliest production stage that must be redone. Null for PASS and FAIL.")
    private ProductionStage targetStage;

}
