package com.smartfactory.common.dto.agv;

import java.time.LocalDateTime;

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
public class EdgeResponse {

    private String edgeId;
    private String sourceNodeId;
    private String targetNodeId;
    private Integer distanceMeters;
    private LocalDateTime createdDate;
}