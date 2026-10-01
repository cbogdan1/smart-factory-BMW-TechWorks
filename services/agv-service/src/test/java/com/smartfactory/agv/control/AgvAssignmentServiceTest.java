package com.smartfactory.agv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.smartfactory.agv.control.exceptions.NoAvailableAgvException;
import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.control.service.AgvAssignmentService;
import com.smartfactory.agv.control.service.RouteCalculatorService;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.AssignAgvRequest;
import com.smartfactory.common.dto.agv.AssignAgvResponse;
import com.smartfactory.common.dto.agv.RouteResponse;
import com.smartfactory.common.enums.AgvStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AgvAssignmentServiceTest {

    @Inject
    AgvAssignmentService agvAssignmentService;

    @InjectMock
    AgvRepository agvRepository;

    @InjectMock
    RouteCalculatorService routeCalculatorService;

    @Test
    void shouldThrowExceptionWhenNoAgvAvailable() {
        // Arrange
        final AssignAgvRequest request =
                AssignAgvRequest.builder().sourceNodeId("WAREHOUSE-A").targetNodeId("LINE-2-PAINT-STATION").build();

        when(agvRepository.findAvailableAgvs()).thenReturn(Collections.emptyList());

        // Act & Assert
        final NoAvailableAgvException exception =
                assertThrows(NoAvailableAgvException.class, () -> agvAssignmentService.assignAgv(request));

        assertEquals("No available AGV in the fleet", exception.getMessage());

        assertEquals("NO_AVAILABLE_AGV", exception.getErrorCode());

        assertEquals(409, exception.getStatusCode());
        verify(agvRepository, never()).persist(any(Agv.class));
    }

    @Test
    void shouldAssignSingleAvailableAgv() {
        // Arrange
        final AssignAgvRequest request =
                AssignAgvRequest.builder().sourceNodeId("WAREHOUSE-A").targetNodeId("LINE-2-PAINT-STATION").build();

        final Node currentNode = new Node();
        currentNode.setNodeId("WAREHOUSE-A");

        final Agv agv = new Agv();
        agv.setAgvId("AGV-001");
        agv.setCurrentNode(currentNode);
        agv.setStatus(AgvStatus.AVAILABLE);

        when(agvRepository.findAvailableAgvs()).thenReturn(List.of(agv));

        final RouteResponse routeResponse = RouteResponse.builder().totalDistanceMeters(50).build();
        when(routeCalculatorService.calculateRoute(any())).thenReturn(routeResponse);

        // Act
        final AssignAgvResponse response = agvAssignmentService.assignAgv(request);

        // Assert
        assertNotNull(response);
        assertEquals("AGV-001", response.getAgvId());
        assertEquals(AgvStatus.BUSY.name(), response.getStatus());

        verify(agvRepository, never()).persist(any(Agv.class));
    }

    @Test
    void shouldAssignAgvWhenMultipleAreAvailable() {
        // Arrange
        final AssignAgvRequest request =
                AssignAgvRequest.builder().sourceNodeId("WAREHOUSE-A").targetNodeId("LINE-2-PAINT-STATION").build();

        final Node node1 = new Node();
        node1.setNodeId("NODE-1");
        final Agv agv1 = new Agv();
        agv1.setAgvId("AGV-001");
        agv1.setCurrentNode(node1);
        agv1.setStatus(AgvStatus.AVAILABLE);

        final Node node2 = new Node();
        node2.setNodeId("NODE-2");
        final Agv agv2 = new Agv();
        agv2.setAgvId("AGV-002");
        agv2.setCurrentNode(node2);
        agv2.setStatus(AgvStatus.AVAILABLE);

        when(agvRepository.findAvailableAgvs()).thenReturn(List.of(agv1, agv2));

        final RouteResponse routeResponse1 = RouteResponse.builder().totalDistanceMeters(100).build();
        final RouteResponse routeResponse2 = RouteResponse.builder().totalDistanceMeters(20).build();

        when(routeCalculatorService.calculateRoute(any())).thenReturn(routeResponse1, routeResponse2);

        // Act
        final AssignAgvResponse response = agvAssignmentService.assignAgv(request);

        // Assert
        assertNotNull(response);
        assertEquals("AGV-002", response.getAgvId());

        assertEquals(AgvStatus.BUSY.name(), response.getStatus());

        verify(agvRepository, never()).persist(any(Agv.class));
    }
}