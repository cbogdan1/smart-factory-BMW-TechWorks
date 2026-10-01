package com.smartfactory.common.dto.quality;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@Schema(name = "InspectionDefectView", description = "A defect recorded during an inspection.")
public class InspectionDefectView {

    @Schema(description = "Code of the standardized defect.", example = "PAINT_SCRATCH")
    private String defectCode;

    @Schema(description = "Description of the standardized defect.", example = "Paint scratch detected")
    private String description;

    @Schema(description = "Penalty points of the standardized defect.", example = "5")
    private Integer penaltyPoints;

    @Schema(description = "Comment added by the inspector when the defect was registered.",
            example = "Scratch on the left door")
    private String comment;
}
