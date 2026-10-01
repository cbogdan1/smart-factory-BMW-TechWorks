package com.smartfactory.dwh.boundary.resource;

import static com.smartfactory.common.EndpointPaths.API_V1;
import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

import java.util.List;

import com.smartfactory.dwh.control.service.EventStoreService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class EventResourceTest {
    @InjectMock
    EventStoreService eventStoreService;

    @Test
    void shouldReturn200() {
        when(eventStoreService.getAllEvents()).thenReturn(List.of());
        given().when().get(API_V1 + "/events").then().statusCode(200).contentType("application/json");
    }

    @Test
    void shouldReturn500WhenServiceFails() {
        when(eventStoreService.getAllEvents()).thenThrow(new RuntimeException("DB error"));
        given().when().get(API_V1 + "/events").then().statusCode(500);
    }

}