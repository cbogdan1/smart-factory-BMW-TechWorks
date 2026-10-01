package com.smartfactory.common.dto.agv;

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
public class AssignAgvResponse {
    private String agvId;
    private String currentNode;
    private Integer distanceToSource;
    private String status;
}
