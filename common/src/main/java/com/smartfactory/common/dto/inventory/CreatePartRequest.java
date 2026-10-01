package com.smartfactory.common.dto.inventory;

import com.smartfactory.common.enums.UnitOfMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartRequest {

    @NotBlank(message = "Part ID is required")
    private String partId;

    @NotBlank(message = "Part code is required")
    private String partCode;

    @NotBlank(message = "Part name is required")
    private String partName;

    private String description;

    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;

    private Boolean active;
}