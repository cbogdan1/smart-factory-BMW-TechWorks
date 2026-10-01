package com.smartfactory.agv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.smartfactory.agv.control.exceptions.NodeNotFoundException;
import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.control.service.AgvService;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.AgvResponse;
import com.smartfactory.common.dto.agv.CreateAgvRequest;
import com.smartfactory.common.enums.AgvStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AgvServiceTest {

    @Inject
    AgvService agvService;

    @InjectMock
    AgvRepository agvRepository;

    @InjectMock
    NodeRepository nodeRepository;

    @Test
    void shouldCreateAgv() {

        final Node node = new Node();
        node.setNodeId("NODE-001");

        final CreateAgvRequest request =
                CreateAgvRequest.builder().status(String.valueOf(AgvStatus.AVAILABLE)).nodeId("NODE-001")
                        .batteryLevel(80).build();

        when(nodeRepository.findByNodeId("NODE-001")).thenReturn(Optional.of(node));

        final AgvResponse response = agvService.createAgv(request);

        assertNotNull(response);
        assertEquals("NODE-001", response.getNodeId());
        assertEquals("AVAILABLE", response.getStatus());
        assertEquals(80, response.getBatteryLevel());

        verify(agvRepository).persist(any(Agv.class));
    }

    @Test
    void shouldThrowWhenNodeDoesNotExist() {

        final CreateAgvRequest request =
                CreateAgvRequest.builder().status(String.valueOf(AgvStatus.AVAILABLE)).nodeId("UNKNOWN")
                        .batteryLevel(80).build();

        when(nodeRepository.findByNodeId("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(NodeNotFoundException.class, () -> agvService.createAgv(request));
    }

    @Test
    void shouldGetAgvById() {

        final Node node = new Node();
        node.setNodeId("NODE-001");

        final Agv agv =
                Agv.builder().agvId("AGV-001").status(AgvStatus.AVAILABLE).currentNode(node).batteryLevel(95).build();

        when(agvRepository.findByAgvId("AGV-001")).thenReturn(Optional.of(agv));

        final AgvResponse response = agvService.getAgv("AGV-001");

        assertEquals("AGV-001", response.getAgvId());
    }

    @Test
    void shouldReturnAllAgvs() {

        final Node node = new Node();
        node.setNodeId("NODE-001");

        final Agv agv = Agv.builder().agvId("AGV-001").status(AgvStatus.AVAILABLE).currentNode(node).batteryLevel(90)
                .createdDate(LocalDateTime.now()).lastUpdated(LocalDateTime.now()).build();

        when(agvRepository.listAll()).thenReturn(List.of(agv));

        final List<AgvResponse> responses = agvService.getAllAgvs();

        assertEquals(1, responses.size());
        assertEquals("AGV-001", responses.get(0).getAgvId());
    }
}