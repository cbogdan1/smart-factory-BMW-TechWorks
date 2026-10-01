package com.smartfactory.common.dto.quality;

import com.smartfactory.common.enums.ProductionStage;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
public class CreateDefectRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String description;

    @Min(0)
    private Integer penaltyPoints;

    @Schema(description = "Production stage affected by this defect (optional).")
    private ProductionStage affectedStage;
}