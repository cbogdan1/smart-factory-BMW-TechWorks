package com.smartfactory.agv.control.service;

import java.util.List;

import com.smartfactory.agv.control.exceptions.NoAssignableAgvException;
import com.smartfactory.agv.control.exceptions.NoAvailableAgvException;
import com.smartfactory.agv.control.exceptions.NoRouteException;
import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.common.dto.agv.AssignAgvRequest;
import com.smartfactory.common.dto.agv.AssignAgvResponse;
import com.smartfactory.common.dto.agv.CreateCalculateRouteRequest;
import com.smartfactory.common.dto.agv.RouteResponse;
import com.smartfactory.common.enums.AgvStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AgvAssignmentService {

    @Inject
    AgvRepository agvRepository;

    @Inject
    RouteCalculatorService routeCalculatorService;

    public AssignAgvResponse assignAgv(final AssignAgvRequest assignAgvRequest) {

        final List<Agv> availableAgvs = agvRepository.findAvailableAgvs();

        if (availableAgvs.isEmpty()) {
            throw new NoAvailableAgvException();
        }

        Agv closestAgv = null;
        int minDistance = Integer.MAX_VALUE;
        for (final Agv agv : availableAgvs) {
            final String agvNode = agv.getCurrentNode().getNodeId();
            final String targetNode = assignAgvRequest.getSourceNodeId();

            if (agvNode.equals(targetNode)) {
                closestAgv = agv;
                closestAgv.setStatus(AgvStatus.BUSY);
                return mapToResponse(closestAgv, 0);
            } else {
                try {
                    final CreateCalculateRouteRequest routeReq =
                            CreateCalculateRouteRequest.builder().sourceNodeId(agv.getCurrentNode().getNodeId())
                                    .targetNodeId(assignAgvRequest.getSourceNodeId()).build();

                    final RouteResponse routeRes = routeCalculatorService.calculateRoute(routeReq);
                    final int currentDistance = routeRes.getTotalDistanceMeters();

                    if (currentDistance < minDistance) {
                        minDistance = currentDistance;
                        closestAgv = agv;
                    }
                } catch (final NoRouteException ignored) {
                }
            }
        }

        if (closestAgv == null) {
            throw new NoAssignableAgvException();
        }

        closestAgv.setStatus(AgvStatus.BUSY);
        return mapToResponse(closestAgv, minDistance);
    }

    private AssignAgvResponse mapToResponse(final Agv agv, final int distance) {
        return AssignAgvResponse.builder().agvId(agv.getAgvId()).currentNode(agv.getCurrentNode().getNodeId())
                .distanceToSource(distance).status(agv.getStatus().name()).build();
    }
}