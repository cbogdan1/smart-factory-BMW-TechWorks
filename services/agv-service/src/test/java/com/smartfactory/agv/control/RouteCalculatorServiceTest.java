package com.smartfactory.agv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.agv.control.exceptions.NoRouteException;
import com.smartfactory.agv.control.repository.EdgeRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.control.service.RouteCalculatorService;
import com.smartfactory.agv.entity.models.Edge;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.CreateCalculateRouteRequest;
import com.smartfactory.common.dto.agv.RouteResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RouteCalculatorServiceTest {

    @Inject
    RouteCalculatorService routeCalculatorService;

    @InjectMock
    NodeRepository nodeRepository;

    @InjectMock
    EdgeRepository edgeRepository;

    private Node node(final String nodeId) {
        final Node node = new Node();
        node.setNodeId(nodeId);
        return node;
    }

    private Edge edge(final Node source, final Node target, final int distance) {

        final Edge edge = new Edge();
        edge.setSourceNode(source);
        edge.setTargetNode(target);
        edge.setDistanceMeters(distance);

        return edge;
    }

    @Test
    void shouldReturnDirectRoute() {

        final Node a = node("A");
        final Node b = node("B");

        final Edge edge = edge(a, b, 10);

        when(nodeRepository.listAll()).thenReturn(List.of(a, b));

        when(edgeRepository.listAll()).thenReturn(List.of(edge));

        final CreateCalculateRouteRequest request = new CreateCalculateRouteRequest("A", "B");

        final RouteResponse response = routeCalculatorService.calculateRoute(request);

        assertEquals(10, response.getTotalDistanceMeters());
        assertEquals(List.of("A", "B"), response.getPath());
    }

    @Test
    void shouldReturnMultiHopPath() {

        final Node a = node("A");
        final Node b = node("B");
        final Node c = node("C");

        when(nodeRepository.listAll()).thenReturn(List.of(a, b, c));

        when(edgeRepository.listAll()).thenReturn(List.of(edge(a, b, 10), edge(b, c, 20)));

        final CreateCalculateRouteRequest request = new CreateCalculateRouteRequest("A", "C");

        final RouteResponse response = routeCalculatorService.calculateRoute(request);

        assertEquals(30, response.getTotalDistanceMeters());

        assertEquals(List.of("A", "B", "C"), response.getPath());
    }

    @Test
    void shouldChooseShortestPath() {

        final Node a = node("A");
        final Node b = node("B");
        final Node c = node("C");
        final Node d = node("D");

        when(nodeRepository.listAll()).thenReturn(List.of(a, b, c, d));

        when(edgeRepository.listAll()).thenReturn(
                List.of(edge(a, b, 10), edge(b, d, 10), edge(a, c, 5), edge(c, d, 5)));

        final CreateCalculateRouteRequest request = new CreateCalculateRouteRequest("A", "D");

        final RouteResponse response = routeCalculatorService.calculateRoute(request);

        assertEquals(10, response.getTotalDistanceMeters());

        assertEquals(List.of("A", "C", "D"), response.getPath());
    }

    @Test
    void shouldThrowWhenRouteDoesNotExist() {

        final Node a = node("A");
        final Node b = node("B");

        when(nodeRepository.listAll()).thenReturn(List.of(a, b));

        when(edgeRepository.listAll()).thenReturn(List.of());

        final CreateCalculateRouteRequest request = new CreateCalculateRouteRequest("A", "B");

        final NoRouteException exception =
                assertThrows(NoRouteException.class, () -> routeCalculatorService.calculateRoute(request));

        assertEquals("No route available from A to B", exception.getMessage());

        assertEquals("NO_ROUTE_FOUND", exception.getErrorCode());

        assertEquals(422, exception.getStatusCode());

    }

    @Test
    void shouldThrowWhenSourceAndTargetAreTheSame() {

        final CreateCalculateRouteRequest request = new CreateCalculateRouteRequest("A", "A");

        final NoRouteException exception =
                assertThrows(NoRouteException.class, () -> routeCalculatorService.calculateRoute(request));

        assertEquals("No route available from A to A", exception.getMessage());

        assertEquals("NO_ROUTE_FOUND", exception.getErrorCode());

        assertEquals(422, exception.getStatusCode());
    }
}
