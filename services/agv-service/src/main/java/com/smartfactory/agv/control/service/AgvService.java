package com.smartfactory.agv.control.service;

import java.util.List;
import java.util.UUID;

import com.smartfactory.agv.control.exceptions.AgvNotFoundException;
import com.smartfactory.agv.control.exceptions.NodeNotFoundException;
import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.AgvResponse;
import com.smartfactory.common.dto.agv.CreateAgvRequest;
import com.smartfactory.common.enums.AgvStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AgvService {

    @Inject
    AgvRepository agvRepository;

    @Inject
    NodeRepository nodeRepository;

    @Transactional
    public AgvResponse createAgv(final CreateAgvRequest request) {

        final Node node = nodeRepository.findByNodeId(request.getNodeId())
                .orElseThrow(() -> new NodeNotFoundException(request.getNodeId()));

        final Agv agv = Agv.builder().agvId(generateAgvIdIfNeeded(request.getAgvId()))
                .status(AgvStatus.valueOf(request.getStatus())).currentNode(node)
                .batteryLevel(request.getBatteryLevel()).build();

        agvRepository.persist(agv);

        return mapToResponse(agv);
    }

    public List<AgvResponse> getAllAgvs() {
        return agvRepository.listAll().stream().map(this::mapToResponse).toList();
    }

    public AgvResponse getAgv(final String agvId) {
        final Agv agv = agvRepository.findByAgvId(agvId).orElseThrow(() -> new AgvNotFoundException(agvId));

        return mapToResponse(agv);
    }

    private AgvResponse mapToResponse(final Agv agv) {
        return AgvResponse.builder().agvId(agv.getAgvId()).status(agv.getStatus().name())
                .nodeId(agv.getCurrentNode().getNodeId()).batteryLevel(agv.getBatteryLevel())
                .createdDate(agv.getCreatedDate()).lastUpdated(agv.getLastUpdated()).build();
    }

    private String generateAgvIdIfNeeded(final String agvId) {
        if (agvId != null && !agvId.isBlank()) {
            return agvId;
        }

        return "AGV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}