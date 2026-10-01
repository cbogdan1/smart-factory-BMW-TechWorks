package com.smartfactory.dwh.boundary.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import com.smartfactory.common.EndpointPaths;
import com.smartfactory.common.dto.dwh.VehicleTwinResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.service.VehicleTwinService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class VehicleTwinResourceTest {

    @InjectMock
    VehicleTwinService vehicleTwinService;

    @Test
    @DisplayName("GET twin - Should return 200 OK with vehicle twin state when found")
    void shouldReturn200WhenVehicleTwinFound() {
        // Arrange
        final String vehicleId = "VEH-001";
        final Instant now = Instant.now();

        final VehicleTwinResponse mockResponse =
                VehicleTwinResponse.builder().vehicleId(vehicleId).orderId("ORD-100").vehicleModel("BMW i4")
                        .status("IN_PROGRESS").currentStage("ASSEMBLY").currentLocation("STATION_01")
                        .qualityStatus("PASSED").reworkCount(0).lastUpdated(now).build();

        when(vehicleTwinService.getVehicleTwin(vehicleId)).thenReturn(mockResponse);

        // Act & Assert
        given().pathParam("vehicleId", vehicleId).when().get(EndpointPaths.TWINS + "/{vehicleId}").then()
                .statusCode(200).body("vehicleId", equalTo(vehicleId)).body("orderId", equalTo("ORD-100"))
                .body("vehicleModel", equalTo("BMW i4")).body("status", equalTo("IN_PROGRESS"))
                .body("currentStage", equalTo("ASSEMBLY")).body("currentLocation", equalTo("STATION_01"))
                .body("qualityStatus", equalTo("PASSED")).body("reworkCount", equalTo(0));

        verify(vehicleTwinService).getVehicleTwin(vehicleId);
    }

    @Test
    @DisplayName("GET twin - Should return 404 Not Found when vehicle twin does not exist")
    void shouldReturn404WhenVehicleTwinNotFound() {
        // Arrange
        final String unknownVehicleId = "VEH-999";

        when(vehicleTwinService.getVehicleTwin(unknownVehicleId)).thenThrow(
                new BusinessException("Vehicle twin not found for vehicleId " + unknownVehicleId, "VEHICLE_NOT_FOUND",
                        404));

        // Act & Assert
        given().pathParam("vehicleId", unknownVehicleId).when().get(EndpointPaths.TWINS + "/{vehicleId}").then()
                .statusCode(404).body("message", equalTo("Vehicle twin not found for vehicleId VEH-999"))
                .body("code", equalTo("VEHICLE_NOT_FOUND"));

        verify(vehicleTwinService).getVehicleTwin(unknownVehicleId);
    }
}
