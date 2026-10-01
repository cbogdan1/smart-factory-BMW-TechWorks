package com.smartfactory.common.dto.agv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CreateEdgeRequest {

    private String edgeId;
    
    @NotBlank(message = "Source node ID cannot be blank")
    private String sourceNodeId;

    @NotBlank(message = "Target node ID cannot be blank")
    private String targetNodeId;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be greater than zero")
    private Integer distanceMeters;
}