package com.smartfactory.common.dto.quality;

import java.time.LocalDateTime;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.ProductionStage;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@Schema(name = "InspectionScoreResponse", description = "Quality score computed for an inspection.")
public class InspectionScoreResponse {
    @Schema(description = "Unique identifier of the inspection.", example = "INSP-9f1c2d1e-6a4b-4f0f-9c1a-2b3c4d5e6f70")
    private String inspectionId;

    @Schema(description = "Identifier of the inspected vehicle.", example = "VEH-001")
    private String vehicleId;

    @Schema(description = "Quality score between 0 and 100. Starts at 100 and every registered defect "
            + "subtracts its penalty points.", example = "75")
    private Integer score;

    @Schema(description = "Number of defects taken into account when the score was computed.", example = "2")
    private Integer defectsCount;

    @Schema(description = "Date and time when the score was computed.", example = "2026-08-04T14:05:00")
    private LocalDateTime calculatedAt;

    @Schema(description = "Automatic decision derived from the score.")
    private Decision decision;

    @Schema(description = "For a REWORK decision, the earliest production stage that must be redone. Null for PASS and FAIL.",
            example = "PAINT")
    private ProductionStage targetStage;
}
