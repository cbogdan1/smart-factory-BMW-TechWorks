package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.ASSIGNMENTS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;

import com.smartfactory.agv.control.service.AgvAssignmentService;
import com.smartfactory.common.dto.agv.AssignAgvRequest;
import com.smartfactory.common.dto.agv.AssignAgvResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
class AgvAssignmentResourceTest {

    @InjectMock
    AgvAssignmentService agvAssignmentService;

    @Test
    void shouldReturn200AndAssignedAgv() {
        // Arrange
        final AssignAgvRequest request =
                AssignAgvRequest.builder().sourceNodeId("WAREHOUSE-A").targetNodeId("LINE-2-PAINT-STATION").build();

        final AssignAgvResponse mockResponse =
                AssignAgvResponse.builder().agvId("AGV-001").currentNode("WAREHOUSE-A").distanceToSource(0)
                        .status("AVAILABLE").build();

        when(agvAssignmentService.assignAgv(any(AssignAgvRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        given().contentType(ContentType.JSON).body(request).when().post(ASSIGNMENTS).then().statusCode(200)
                .body("agvId", is("AGV-001")).body("status", is("AVAILABLE"));
    }

    @Test
    void shouldReturn409WhenNoAgvAvailable() {
        // Arrange
        final AssignAgvRequest request =
                AssignAgvRequest.builder().sourceNodeId("WAREHOUSE-A").targetNodeId("LINE-2-PAINT-STATION").build();

        when(agvAssignmentService.assignAgv(any(AssignAgvRequest.class))).thenThrow(new WebApplicationException(
                Response.status(409).entity(Map.of("message", "No available AGV in the fleet")).build()));

        // Act & Assert
        given().contentType(ContentType.JSON).body(request).when().post(ASSIGNMENTS).then().statusCode(409)
                .body("message", is("No available AGV in the fleet"));
    }

    @Test
    void shouldReturn400ForInvalidPayload() {
        // Arrange
        final AssignAgvRequest request = AssignAgvRequest.builder().targetNodeId("LINE-2-PAINT-STATION").build();

        // Act & Assert
        given().contentType(ContentType.JSON).body(request).when().post(ASSIGNMENTS).then().statusCode(400);
    }
}