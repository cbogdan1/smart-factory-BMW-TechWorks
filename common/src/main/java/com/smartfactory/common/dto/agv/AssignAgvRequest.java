package com.smartfactory.common.dto.agv;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignAgvRequest {

    @NotBlank(message = "Source node ID cannot be blank")
    private String sourceNodeId;
    @NotBlank(message = "Target node ID cannot be blank")
    private String targetNodeId;
}
