package com.smartfactory.common.dto.agv;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAgvRequest {

    private String agvId;

    @NotNull
    private String status;

    @NotBlank
    private String nodeId;

    @Min(0)
    @Max(100)
    private Integer batteryLevel;
}