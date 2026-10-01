package com.smartfactory.dwh.boundary.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import com.smartfactory.common.dto.dwh.VehicleTimelineResponse;
import com.smartfactory.dwh.control.service.VehicleTimelineService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
class VehicleTimelineResourceTest {

    @InjectMock
    VehicleTimelineService timelineService;

    @Test
    @DisplayName("GET /api/v1/timeline/{vehicleId} - Should return 200 OK with ordered timeline items")
    void shouldReturn200AndOrderedTimelineWhenVehicleExists() {
        // Arrange
        final String vehicleId = "VEH-001";
        final Instant t1 = Instant.parse("2026-08-01T10:15:00Z");
        final Instant t2 = Instant.parse("2026-08-01T10:30:00Z");
        final VehicleTimelineResponse event1 = new VehicleTimelineResponse("order-created", "order-service", t1);
        final VehicleTimelineResponse event2 =
                new VehicleTimelineResponse("production-planned", "planning-service", t2);
        when(timelineService.getVehicleTimeline(vehicleId)).thenReturn(List.of(event1, event2));

        // Act, Assert
        given().pathParam("vehicleId", vehicleId).when().get("api/v1/timeline/{vehicleId}").then().statusCode(200)
                .body("$.size()", is(2)).body("[0].eventType", equalTo("order-created"))
                .body("[0].sourceService", equalTo("order-service"))
                .body("[0].eventTimestamp", equalTo("2026-08-01T10:15:00Z"))
                .body("[1].eventType", equalTo("production-planned"))
                .body("[1].sourceService", equalTo("planning-service"))
                .body("[1].eventTimestamp", equalTo("2026-08-01T10:30:00Z"));
        Mockito.verify(timelineService).getVehicleTimeline(vehicleId);
    }

    @Test
    @DisplayName("GET /api/timeline/{vehicleId} - Should return 404 Not Found when vehicle has no timeline")
    void shouldReturn404WhenVehicleNotFound() {
        // Arrange
        final String unknownVehicleId = "VEH-UNKNOWN";
        when(timelineService.getVehicleTimeline(unknownVehicleId)).thenThrow(
                new NotFoundException("No timeline found for vehicleId: " + unknownVehicleId));

        // Act, Assert
        given().pathParam("vehicleId", unknownVehicleId).when().get("/api/v1/timeline/{vehicleId}").then()
                .statusCode(404);
        Mockito.verify(timelineService).getVehicleTimeline(unknownVehicleId);
    }
}