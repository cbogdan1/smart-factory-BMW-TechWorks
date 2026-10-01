package com.smartfactory.common.dto.agv;

import java.util.List;

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
public class RouteResponse {
    private String sourceNodeId;
    private String targetNodeId;
    private Integer totalDistanceMeters;
    private List<String> path;
}
