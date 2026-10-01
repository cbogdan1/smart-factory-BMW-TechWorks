package com.smartfactory.agv.control.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import com.smartfactory.agv.control.exceptions.InvalidRouteRequestException;
import com.smartfactory.agv.control.exceptions.NoRouteException;
import com.smartfactory.agv.control.repository.EdgeRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.entity.models.Edge;
import com.smartfactory.common.dto.agv.CreateCalculateRouteRequest;
import com.smartfactory.common.dto.agv.RouteResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RouteCalculatorService {

    @Inject
    NodeRepository nodeRepository;

    @Inject
    EdgeRepository edgeRepository;

    public RouteResponse calculateRoute(final CreateCalculateRouteRequest request) {
        final String sourceNodeId = request.getSourceNodeId();
        final String targetNodeId = request.getTargetNodeId();

        if (sourceNodeId == null || targetNodeId == null) {
            throw new InvalidRouteRequestException();
        }
        if (sourceNodeId.equals(targetNodeId)) {
            throw new NoRouteException(sourceNodeId, targetNodeId);
        }

        final Map<String, List<Edge>> adjacency = edgeRepository.listAll().stream()
                .collect(Collectors.groupingBy(edge -> edge.getSourceNode().getNodeId()));
        final Map<String, Integer> distance = new HashMap<>();
        final Map<String, String> previous = new HashMap<>();

        nodeRepository.listAll().forEach(node -> distance.put(node.getNodeId(), Integer.MAX_VALUE));

        distance.put(sourceNodeId, 0);

        final PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(NodeDistance::distance));
        queue.add(new NodeDistance(sourceNodeId, 0));

        final Set<String> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            final NodeDistance current = queue.poll();

            if (visited.contains(current.nodeId())) {
                continue;
            }
            visited.add(current.nodeId());

            if (current.nodeId().equals(targetNodeId)) {
                break;
            }

            for (final Edge edge : adjacency.getOrDefault(current.nodeId(), List.of())) {
                final String neighbour = edge.getTargetNode().getNodeId();

                final int currentDist = distance.get(current.nodeId());

                if (currentDist == Integer.MAX_VALUE) {
                    continue;
                }

                final int newDistance = currentDist + edge.getDistanceMeters();

                if (newDistance < distance.getOrDefault(neighbour, Integer.MAX_VALUE)) {
                    distance.put(neighbour, newDistance);
                    previous.put(neighbour, current.nodeId());
                    queue.add(new NodeDistance(neighbour, newDistance));
                }
            }
        }

        if (distance.get(targetNodeId) == null || distance.get(targetNodeId) == Integer.MAX_VALUE) {
            throw new NoRouteException(sourceNodeId, targetNodeId);
        }

        final List<String> path = new ArrayList<>();
        String current = targetNodeId;

        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        Collections.reverse(path);

        return RouteResponse.builder().sourceNodeId(sourceNodeId).targetNodeId(targetNodeId)
                .totalDistanceMeters(distance.get(targetNodeId)).path(Collections.unmodifiableList(path)).build();
    }

    private record NodeDistance(String nodeId, int distance) {
    }
}