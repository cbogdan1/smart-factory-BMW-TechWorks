package com.smartfactory.agv.control.service;

import java.util.List;

import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.control.repository.EdgeRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.agv.entity.models.Edge;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.FactoryGraphResponse;
import com.smartfactory.common.dto.agv.GraphAgvResponse;
import com.smartfactory.common.dto.agv.GraphEdgeResponse;
import com.smartfactory.common.dto.agv.GraphNodeResponse;
import com.smartfactory.common.enums.NodeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GraphService {

    @Inject
    NodeRepository nodeRepository;

    @Inject
    EdgeRepository edgeRepository;

    @Inject
    AgvRepository agvRepository;

    public FactoryGraphResponse getFactoryGraph() {

        final List<GraphNodeResponse> nodes = nodeRepository.listAll().stream().map(this::mapNode).toList();

        final List<GraphEdgeResponse> edges = edgeRepository.listAll().stream().map(this::mapEdge).toList();

        final List<GraphAgvResponse> agvs = agvRepository.listAll().stream().map(this::mapAgv).toList();

        return FactoryGraphResponse.builder().nodes(nodes).edges(edges).agvs(agvs).build();
    }

    private GraphNodeResponse mapNode(final Node node) {
        return GraphNodeResponse.builder().nodeId(node.getNodeId()).name(node.getName())
                .type(NodeType.valueOf(node.getType().name())).build();
    }

    private GraphEdgeResponse mapEdge(final Edge edge) {
        return GraphEdgeResponse.builder().edgeId(edge.getEdgeId()).sourceNode(edge.getSourceNode().getName())
                .targetNode(edge.getTargetNode().getName()).distanceMeters(edge.getDistanceMeters()).build();
    }

    private GraphAgvResponse mapAgv(final Agv agv) {
        return GraphAgvResponse.builder().agvId(agv.getAgvId()).status(agv.getStatus().name())
                .currentNode(agv.getCurrentNode().getName()).batteryLevel(agv.getBatteryLevel()).build();
    }
}