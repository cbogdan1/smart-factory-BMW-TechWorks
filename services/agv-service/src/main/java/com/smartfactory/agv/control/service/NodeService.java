package com.smartfactory.agv.control.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.smartfactory.agv.control.exceptions.InvalidNodeTypeException;
import com.smartfactory.agv.control.exceptions.NodeAlreadyExistsException;
import com.smartfactory.agv.control.exceptions.NodeNameAlreadyExistsException;
import com.smartfactory.agv.control.exceptions.NodeNotFoundException;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.CreateNodeRequest;
import com.smartfactory.common.dto.agv.NodeResponse;
import com.smartfactory.common.enums.NodeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class NodeService {
    @Inject
    NodeRepository nodeRepository;

    @Transactional
    public NodeResponse createNode(final CreateNodeRequest request) {

        final String finalNodeId;
        if (request.getNodeId() == null || request.getNodeId().trim().isEmpty()) {
            finalNodeId = "NODE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } else {
            finalNodeId = request.getNodeId();
        }

        final boolean nodeIdExists = nodeRepository.findByNodeId(finalNodeId).isPresent();
        if (nodeIdExists) {
            throw new NodeAlreadyExistsException(finalNodeId);
        }

        final boolean nameExists = nodeRepository.findByName(request.getName()).isPresent();
        if (nameExists) {
            throw new NodeNameAlreadyExistsException(request.getName());
        }

        final NodeType nodeType;
        try {
            nodeType = NodeType.valueOf(request.getType().toUpperCase());
        } catch (final IllegalArgumentException e) {
            throw new InvalidNodeTypeException();
        }

        final Node node = Node.builder().nodeId(finalNodeId).name(request.getName()).type(nodeType).build();

        nodeRepository.persist(node);

        return mapToResponse(node);
    }

    public List<NodeResponse> getAllNodes() {
        return nodeRepository.listAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public NodeResponse getNodeById(final String nodeId) {
        final Node node = nodeRepository.findByNodeId(nodeId).orElseThrow(() -> new NodeNotFoundException(nodeId));

        return mapToResponse(node);
    }

    private NodeResponse mapToResponse(final Node node) {
        return NodeResponse.builder().nodeId(node.getNodeId()).name(node.getName()).type(node.getType().name())
                .createdDate(node.getCreatedDate()).build();
    }
}