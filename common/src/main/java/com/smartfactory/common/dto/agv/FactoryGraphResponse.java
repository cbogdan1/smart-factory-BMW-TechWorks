package com.smartfactory.common.dto.agv;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FactoryGraphResponse {

    private List<GraphNodeResponse> nodes;
    private List<GraphEdgeResponse> edges;
    private List<GraphAgvResponse> agvs;
}