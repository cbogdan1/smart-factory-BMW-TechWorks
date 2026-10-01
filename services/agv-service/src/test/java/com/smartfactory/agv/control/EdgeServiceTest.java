package com.smartfactory.agv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import com.smartfactory.agv.control.exceptions.SourceNodeNotFoundException;
import com.smartfactory.agv.control.repository.EdgeRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.control.service.EdgeService;
import com.smartfactory.agv.entity.models.Edge;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.CreateEdgeRequest;
import com.smartfactory.common.dto.agv.EdgeResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EdgeServiceTest {

    @Inject
    EdgeService edgeService;

    @InjectMock
    EdgeRepository edgeRepository;

    @InjectMock
    NodeRepository nodeRepository;

    @Test
    void shouldCreateEdge() {
        // Arrange
        final CreateEdgeRequest request =
                CreateEdgeRequest.builder().edgeId("EDGE-TEST").sourceNodeId("NODE-1").targetNodeId("NODE-2")
                        .distanceMeters(25).build();

        final Node sourceNode = new Node();
        sourceNode.setId(1L);
        sourceNode.setNodeId("NODE-1");

        final Node targetNode = new Node();
        targetNode.setId(2L);
        targetNode.setNodeId("NODE-2");

        when(nodeRepository.findByNodeId("NODE-1")).thenReturn(Optional.of(sourceNode));
        when(nodeRepository.findByNodeId("NODE-2")).thenReturn(Optional.of(targetNode));
        when(edgeRepository.findByEdgeId("EDGE-TEST")).thenReturn(Optional.empty());

        // Act
        final EdgeResponse response = edgeService.createEdge(request);

        // Assert
        assertNotNull(response);
        assertEquals(25, response.getDistanceMeters());

        verify(edgeRepository, times(1)).persist(any(Edge.class));
    }

    @Test
    void shouldThrowExceptionWhenNodeNotFound() {
        // Arrange
        final CreateEdgeRequest request =
                CreateEdgeRequest.builder().sourceNodeId("NODE-99").targetNodeId("NODE-2").distanceMeters(10).build();

        when(nodeRepository.findByNodeId("NODE-99")).thenReturn(Optional.empty());

        // Act & Assert
        final SourceNodeNotFoundException exception = assertThrows(SourceNodeNotFoundException.class, () -> {
            edgeService.createEdge(request);
        });

        assertEquals("Source node not found: NODE-99", exception.getMessage());
        verify(edgeRepository, never()).persist(any(Edge.class));
    }

    @Test
    void shouldReturnEdgeById() {
        // Arrange
        final String edgeId = "EDGE-12345678";

        final Node sourceNode = new Node();
        sourceNode.setId(1L);
        sourceNode.setNodeId("NODE-1");

        final Node targetNode = new Node();
        targetNode.setId(2L);
        targetNode.setNodeId("NODE-2");

        final Edge edge = new Edge();
        edge.setId(1L);
        edge.setEdgeId(edgeId);
        edge.setSourceNode(sourceNode);
        edge.setTargetNode(targetNode);
        edge.setDistanceMeters(15);
        edge.setCreatedDate(LocalDateTime.now());

        when(edgeRepository.findByEdgeId(edgeId)).thenReturn(Optional.of(edge));

        // Act
        final EdgeResponse response = edgeService.getEdgeById(edgeId);

        // Assert
        assertNotNull(response);
        assertEquals(edgeId, response.getEdgeId());
        assertEquals(15, response.getDistanceMeters());
    }
}