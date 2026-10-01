package com.smartfactory.agv.boundary;

import static com.smartfactory.common.EndpointPaths.NODES;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.any;

import java.util.List;

import com.smartfactory.common.dto.agv.CreateNodeRequest;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class NodeResourceTest {

    @Test
    void shouldCreateNode() {
        final CreateNodeRequest request =
                CreateNodeRequest.builder().nodeId("NODE-PAINT").name("Painting Station").type("WAREHOUSE").build();

        given().contentType(ContentType.JSON).body(request).when().post(NODES).then().statusCode(201)
                .body("nodeId", startsWith("NODE-")).body("name", is("Painting Station")).body("type", is("WAREHOUSE"));
    }

    @Test
    void shouldReturn400ForInvalidPayload() {
        final CreateNodeRequest request = CreateNodeRequest.builder().name("").type("WAREHOUSE").build();

        given().contentType(ContentType.JSON).body(request).when().post(NODES).then().statusCode(400);
    }

    @Test
    void shouldReturnAllNodes() {
        given().when().get(NODES).then().statusCode(200).body("$", any(List.class));
    }

    @Test
    void shouldReturn404WhenNodeNotFound() {

        given().when().get(NODES + "/NODE-INVALID").then().statusCode(404);
    }
}