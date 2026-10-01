package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.GRAPH;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.agv.control.service.GraphService;
import com.smartfactory.common.dto.agv.FactoryGraphResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GraphResourceTest {

    @InjectMock
    GraphService graphService;

    @Test
    void shouldReturnFactoryGraph() {

        final FactoryGraphResponse response =
                FactoryGraphResponse.builder().nodes(List.of()).edges(List.of()).agvs(List.of()).build();

        when(graphService.getFactoryGraph()).thenReturn(response);

        RestAssured.given().accept(MediaType.APPLICATION_JSON).when().get(GRAPH).then().statusCode(200);
    }
}