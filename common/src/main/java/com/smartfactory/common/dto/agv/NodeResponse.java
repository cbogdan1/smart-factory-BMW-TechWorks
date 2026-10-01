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
public class NodeResponse {

    private String nodeId;
    private String name;
    private String type;
    private LocalDateTime createdDate;
}