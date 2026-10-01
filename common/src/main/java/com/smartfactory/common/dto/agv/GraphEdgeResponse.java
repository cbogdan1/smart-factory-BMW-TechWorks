package com.smartfactory.common.dto.agv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraphEdgeResponse {

    private String edgeId;
    private String sourceNode;
    private String targetNode;
    private Integer distanceMeters;
}