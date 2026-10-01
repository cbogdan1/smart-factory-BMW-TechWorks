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

import com.smartfactory.agv.control.exceptions.NodeNameAlreadyExistsException;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.control.service.NodeService;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.CreateNodeRequest;
import com.smartfactory.common.dto.agv.NodeResponse;
import com.smartfactory.common.enums.NodeType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class NodeServiceTest {

    @Inject
    NodeService nodeService;

    @InjectMock
    NodeRepository nodeRepository;

    @Test
    void shouldCreateNode() {
        // Arrange
        final CreateNodeRequest request =
                CreateNodeRequest.builder().nodeId("NODE-TEST").name("Assembly Station").type("WAREHOUSE").build();

        when(nodeRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(nodeRepository.findByNodeId(request.getNodeId())).thenReturn(Optional.empty());

        // Act
        final NodeResponse response = nodeService.createNode(request);

        // Assert
        assertNotNull(response);
        assertEquals("Assembly Station", response.getName());
        assertEquals("WAREHOUSE", response.getType());

        verify(nodeRepository, times(1)).persist(any(Node.class));
    }

    @Test
    void shouldThrowBadRequestWhenNodeNameExists() {
        // Arrange
        final CreateNodeRequest request =
                CreateNodeRequest.builder().nodeId("NODE-TEST").name("Assembly Station").type("WAREHOUSE").build();
        final Node existingNode = new Node();

        when(nodeRepository.findByName(request.getName())).thenReturn(Optional.of(existingNode));

        // Act & Assert
        final NodeNameAlreadyExistsException exception = assertThrows(NodeNameAlreadyExistsException.class, () -> {
            nodeService.createNode(request);
        });

        assertEquals("Name already exists: Assembly Station", exception.getMessage());
        verify(nodeRepository, never()).persist(any(Node.class));
    }

    @Test
    void shouldReturnNodeById() {
        // Arrange
        final String nodeId = "NODE-12345678";
        final Node node = new Node();
        node.setNodeId(nodeId);
        node.setName("Test Node");
        node.setType(NodeType.BUFFER);
        node.setCreatedDate(LocalDateTime.now());

        when(nodeRepository.findByNodeId(nodeId)).thenReturn(Optional.of(node));

        // Act
        final NodeResponse response = nodeService.getNodeById(nodeId);

        // Assert
        assertNotNull(response);
        assertEquals(nodeId, response.getNodeId());
        assertEquals("Test Node", response.getName());
        assertEquals("BUFFER", response.getType());
    }
}