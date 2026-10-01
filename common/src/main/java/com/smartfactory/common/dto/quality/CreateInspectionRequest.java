package com.smartfactory.common.dto.quality;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateInspectionRequest {

    @NotBlank
    private String vehicleId;

    // Optional: who performed the inspection (inspector name)
    private String inspector;

}
