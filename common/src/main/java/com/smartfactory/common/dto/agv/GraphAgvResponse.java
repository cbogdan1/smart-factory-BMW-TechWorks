package com.smartfactory.common.dto.agv;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GraphAgvResponse {

    private String agvId;
    private String status;
    private String currentNode;
    private Integer batteryLevel;
}