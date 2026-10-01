package com.smartfactory.common.dto.agv;

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
public class CreateNodeRequest {
    
    private String nodeId;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Type cannot be null")
    private String type;
}