package com.smartfactory.agv.control.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.smartfactory.agv.control.exceptions.EdgeAlreadyExistsException;
import com.smartfactory.agv.control.exceptions.EdgeNotFoundException;
import com.smartfactory.agv.control.exceptions.SourceNodeNotFoundException;
import com.smartfactory.agv.control.exceptions.TargetNodeNotFoundException;
import com.smartfactory.agv.control.repository.EdgeRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.entity.models.Edge;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.CreateEdgeRequest;
import com.smartfactory.common.dto.agv.EdgeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EdgeService {

    @Inject
    EdgeRepository edgeRepository;

    @Inject
    NodeRepository nodeRepository;

    @Transactional
    public EdgeResponse createEdge(final CreateEdgeRequest request) {
        final Node source = nodeRepository.findByNodeId(request.getSourceNodeId())
                .orElseThrow(() -> new SourceNodeNotFoundException(request.getSourceNodeId()));

        final Node target = nodeRepository.findByNodeId(request.getTargetNodeId())
                .orElseThrow(() -> new TargetNodeNotFoundException(request.getTargetNodeId()));

        final String finalEdgeId;
        if (request.getEdgeId() == null || request.getEdgeId().trim().isEmpty()) {
            finalEdgeId = "EDGE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else {
            finalEdgeId = request.getEdgeId();
        }

        if (edgeRepository.findByEdgeId(finalEdgeId).isPresent()) {
            throw new EdgeAlreadyExistsException(finalEdgeId);
        }

        final Edge edge =
                Edge.builder().sourceNode(source).targetNode(target).distanceMeters(request.getDistanceMeters())
                        .edgeId(finalEdgeId).build();

        edgeRepository.persist(edge);

        return mapToResponse(edge);
    }

    public List<EdgeResponse> getAllEdges() {
        return edgeRepository.listAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public EdgeResponse getEdgeById(final String edgeId) {
        final Edge edge = edgeRepository.findByEdgeId(edgeId).orElseThrow(() -> new EdgeNotFoundException(edgeId));

        return mapToResponse(edge);
    }

    private EdgeResponse mapToResponse(final Edge edge) {
        return EdgeResponse.builder().edgeId(edge.getEdgeId()).sourceNodeId(edge.getSourceNode().getNodeId())
                .targetNodeId(edge.getTargetNode().getNodeId()).distanceMeters(edge.getDistanceMeters())
                .createdDate(edge.getCreatedDate()).build();
    }
}