package com.smartfactory.common.dto.quality;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDefectToInspectionRequest {

    @NotBlank
    private String defectCode;

    @Size(max = 500)
    private String comment;
}
