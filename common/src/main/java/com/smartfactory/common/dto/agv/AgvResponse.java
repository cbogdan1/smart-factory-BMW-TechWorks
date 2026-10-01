package com.smartfactory.common.dto.agv;

import java.time.LocalDateTime;

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
public class AgvResponse {

    private String agvId;
    private String status;
    private String nodeId;
    private Integer batteryLevel;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdated;
}