package com.smartfactory.planning.boundary;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PingResourceTest {
    @Test
    void shouldReturnServiceHealth() {
        given().when().get("/api/ping").then().statusCode(200).body("service", is("planning-service"))
                .body("status", is("UP"));
    }
}