package com.smartfactory.common.dto.quality;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.InspectionStatus;
import com.smartfactory.common.enums.ProductionStage;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@Schema(name = "InspectionView", description = "Complete inspection report, including all registered defects.")
public class InspectionView {

    @Schema(description = "Unique identifier of the inspection.", example = "INSP-9f1c2d1e-6a4b-4f0f-9c1a-2b3c4d5e6f70")
    private String inspectionId;

    @Schema(description = "Identifier of the inspected vehicle.", example = "VEH-001")
    private String vehicleId;

    @Schema(description = "Quality score computed for this inspection. Null while it has never been calculated.",
            example = "75")
    private Integer score;

    @Schema(description = "Date and time when the inspection was created.", example = "2026-08-04T10:15:30")
    private LocalDateTime inspectionDate;

    @Schema(description = "Current status of the inspection.", example = "PENDING")
    private InspectionStatus status;

    @Schema(description = "Defects registered during the inspection. Empty when no defect was registered.")
    private List<InspectionDefectView> defects = new ArrayList<>();

    @Schema(description = "Automatic decision derived from the score.", example = "REWORK")
    private Decision decision;

    @Schema(description = "For a REWORK decision, the earliest production stage that must be redone. Null for PASS and FAIL.",
            example = "PAINT")
    private ProductionStage targetStage;

    @Schema(description = "Inspector who performed the inspection.", example = "John Smith")
    private String inspector;
}
