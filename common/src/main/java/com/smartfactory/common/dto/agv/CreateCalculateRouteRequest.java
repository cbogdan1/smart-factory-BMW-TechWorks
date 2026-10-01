package com.smartfactory.common.dto.agv;

import jakarta.validation.constraints.NotBlank;
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
public class CreateCalculateRouteRequest {
    @NotBlank
    private String sourceNodeId;
    @NotBlank
    private String targetNodeId;
}
