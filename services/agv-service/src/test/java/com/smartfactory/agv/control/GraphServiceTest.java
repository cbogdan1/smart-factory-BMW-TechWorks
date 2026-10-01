package com.smartfactory.agv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.control.repository.EdgeRepository;
import com.smartfactory.agv.control.repository.NodeRepository;
import com.smartfactory.agv.control.service.GraphService;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.agv.entity.models.Edge;
import com.smartfactory.agv.entity.models.Node;
import com.smartfactory.common.dto.agv.FactoryGraphResponse;
import com.smartfactory.common.enums.AgvStatus;
import com.smartfactory.common.enums.NodeType;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GraphServiceTest {

    @Inject
    GraphService graphService;

    @InjectMock
    NodeRepository nodeRepository;

    @InjectMock
    EdgeRepository edgeRepository;

    @InjectMock
    AgvRepository agvRepository;

    @Test
    void shouldReturnCompleteFactoryGraph() {

        final Node warehouse = new Node();
        warehouse.setNodeId("NODE-001");
        warehouse.setName("WH-BATTERY");
        warehouse.setType(NodeType.WAREHOUSE);

        final Node junction = new Node();
        junction.setNodeId("NODE-002");
        junction.setName("JUNCTION-A");
        junction.setType(NodeType.JUNCTION);

        final Edge edge = new Edge();
        edge.setEdgeId("EDGE-001");
        edge.setSourceNode(warehouse);
        edge.setTargetNode(junction);
        edge.setDistanceMeters(30);

        final Agv agv = new Agv();
        agv.setAgvId("AGV-001");
        agv.setStatus(AgvStatus.AVAILABLE);
        agv.setBatteryLevel(95);
        agv.setCurrentNode(junction);

        when(nodeRepository.listAll()).thenReturn(List.of(warehouse, junction));
        when(edgeRepository.listAll()).thenReturn(List.of(edge));
        when(agvRepository.listAll()).thenReturn(List.of(agv));

        final FactoryGraphResponse response = graphService.getFactoryGraph();

        assertEquals(2, response.getNodes().size());
        assertEquals(1, response.getEdges().size());
        assertEquals(1, response.getAgvs().size());

        assertEquals("WH-BATTERY", response.getEdges().getFirst().getSourceNode());
        assertEquals("JUNCTION-A", response.getEdges().getFirst().getTargetNode());
        assertEquals("AVAILABLE", response.getAgvs().getFirst().getStatus());

        assertEquals("JUNCTION-A", response.getAgvs().getFirst().getCurrentNode());
    }

    @Test
    void shouldReturnEmptyGraph() {

        when(nodeRepository.listAll()).thenReturn(List.of());
        when(edgeRepository.listAll()).thenReturn(List.of());
        when(agvRepository.listAll()).thenReturn(List.of());

        final FactoryGraphResponse response = graphService.getFactoryGraph();

        assertEquals(0, response.getNodes().size());
        assertEquals(0, response.getEdges().size());
        assertEquals(0, response.getAgvs().size());
    }
}