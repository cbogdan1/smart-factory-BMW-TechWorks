package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.EDGES;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.agv.control.service.EdgeService;

import com.smartfactory.common.dto.agv.CreateEdgeRequest;
import com.smartfactory.common.dto.agv.EdgeResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class EdgeResourceTest {
    @InjectMock
    EdgeService edgeService;

    @Test
    void shouldCreateEdge() {
        final CreateEdgeRequest request =
                CreateEdgeRequest.builder().sourceNodeId("NODE-1").targetNodeId("NODE-2").distanceMeters(12).build();

        final EdgeResponse mockResponse = new EdgeResponse();
        mockResponse.setEdgeId("EDGE-12345678");
        mockResponse.setDistanceMeters(12);

        when(edgeService.createEdge(Mockito.any(CreateEdgeRequest.class))).thenReturn(mockResponse);

        given().contentType(ContentType.JSON).body(request).when().post(EDGES).then().statusCode(201)
                .body("distanceMeters", is(12)).body("edgeId", notNullValue());
    }

    @Test
    void shouldReturn400ForInvalidPayload() {
        final CreateEdgeRequest request =
                CreateEdgeRequest.builder().sourceNodeId("NODE-1").targetNodeId("NODE-2").distanceMeters(-5).build();

        given().contentType(ContentType.JSON).body(request).when().post(EDGES).then().statusCode(400);
    }

    @Test
    void shouldReturnAllEdges() {
        when(edgeService.getAllEdges()).thenReturn(List.of(new EdgeResponse()));

        given().when().get(EDGES).then().statusCode(200).body("$", any(List.class));
    }

    @Test
    void shouldReturn404WhenEdgeNotFound() {
        when(edgeService.getEdgeById("EDGE-9999")).thenThrow(new NotFoundException("Edge not found"));

        given().when().get(EDGES + "/EDGE-9999").then().statusCode(404);
    }
}