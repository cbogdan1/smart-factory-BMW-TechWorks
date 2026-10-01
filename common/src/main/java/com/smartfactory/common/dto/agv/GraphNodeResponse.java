package com.smartfactory.common.dto.agv;

import com.smartfactory.common.enums.NodeType;
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
public class GraphNodeResponse {

    private String nodeId;
    private String name;
    private NodeType type;
}
