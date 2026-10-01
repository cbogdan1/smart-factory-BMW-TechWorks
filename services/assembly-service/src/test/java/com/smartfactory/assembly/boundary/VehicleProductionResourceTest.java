package com.smartfactory.assembly.boundary;

import static com.smartfactory.common.EndpointPaths.VEHICLES;
import static com.smartfactory.common.enums.ProductionStage.CREATED;
import static com.smartfactory.common.enums.ProductionStage.BODY;
import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import com.smartfactory.assembly.control.services.VehicleProductionService;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.dto.assembly.VehicleProductionResponse;
import com.smartfactory.common.dto.assembly.VehicleProductionView;
import com.smartfactory.common.enums.ProductionStatus;
import com.smartfactory.common.exception.BusinessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleProductionResourceTest {

    @Mock
    VehicleProductionService service;

    @InjectMocks
    VehicleProductionResource resource;

    @Test
    void shouldCreateVehicleProductionAndReturnCreatedResponse() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final CreateVehicleProductionRequest request =
                new CreateVehicleProductionRequest(vehicleId, orderId, "BMW_I4", "LINE-1");
        final VehicleProductionResponse expectedResponse = createResponse(vehicleId, orderId);

        when(service.create(request)).thenReturn(expectedResponse);

        try (final Response response = resource.create(request)) {
            assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
            assertSame(expectedResponse, response.getEntity());
            assertEquals(VEHICLES + "/" + vehicleId, response.getLocation().getPath());
        }

        verify(service).create(request);
    }

    @Test
    void shouldFindVehicleProductionByVehicleId() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final VehicleProductionView expectedView = createView(vehicleId, orderId);

        when(service.findByVehicleId(vehicleId)).thenReturn(expectedView);

        final VehicleProductionView actualView = resource.findByVehicleId(vehicleId);

        assertSame(expectedView, actualView);
        verify(service).findByVehicleId(vehicleId);
    }

    @Test
    void shouldThrowBusinessExceptionWhenVehicleProductionDoesNotExist() {
        final String vehicleId = "VEH-001";
        final String expectedMessage = String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId);

        when(service.findByVehicleId(vehicleId)).thenThrow(
                new BusinessException(expectedMessage, VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, 404));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> resource.findByVehicleId(vehicleId));

        assertEquals(404, exception.getStatusCode());
        assertEquals(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());

        verify(service).findByVehicleId(vehicleId);
    }

    @Test
    void shouldReturnAllVehicleProductionRecords() {
        final VehicleProductionResponse firstResponse =
                createResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final VehicleProductionResponse secondResponse =
                createResponse(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final List<VehicleProductionResponse> expectedResponses = List.of(firstResponse, secondResponse);

        when(service.findAll()).thenReturn(expectedResponses);

        final List<VehicleProductionResponse> actualResponses = resource.findAll();

        assertSame(expectedResponses, actualResponses);
        assertEquals(2, actualResponses.size());
        verify(service).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoVehicleProductionsExist() {
        final List<VehicleProductionResponse> expectedResponses = List.of();

        when(service.findAll()).thenReturn(expectedResponses);

        final List<VehicleProductionResponse> actualResponses = resource.findAll();

        assertSame(expectedResponses, actualResponses);
        assertEquals(0, actualResponses.size());
        verify(service).findAll();
    }

    @Test
    void shouldAdvanceVehicleStateSuccessfully() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final VehicleProductionResponse expectedResponse = createResponse(vehicleId, orderId);

        when(service.advanceStage(vehicleId)).thenReturn(expectedResponse);

        try (final Response response = resource.advanceVehicleState(vehicleId)) {
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            assertEquals(expectedResponse, response.getEntity());
            assertEquals(
                    UriBuilder.fromResource(VehicleProductionResource.class).path(expectedResponse.vehicleId()).build(),
                    response.getLocation());
        }

        verify(service).advanceStage(vehicleId);
    }

    @Test
    void shouldThrowBusinessExceptionWhenAdvancingUnknownVehicle() {
        final String vehicleId = "VEH-001";
        final String expectedMessage = String.format(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_MESSAGE, vehicleId);

        when(service.advanceStage(vehicleId)).thenThrow(
                new BusinessException(expectedMessage, VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, 404));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> resource.advanceVehicleState(vehicleId));

        assertEquals(404, exception.getStatusCode());
        assertEquals(VEHICLE_PRODUCTION_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());

        verify(service).advanceStage(vehicleId);
    }

    @Test
    void shouldThrowBusinessExceptionWhenAdvancingVehicleWithInvalidStatus() {
        final String vehicleId = "VEH-001";
        final String expectedMessage = String.format(INVALID_VEHICLE_STATUS_ERROR_MESSAGE, vehicleId);

        when(service.advanceStage(vehicleId)).thenThrow(
                new BusinessException(expectedMessage, INVALID_VEHICLE_STATUS_ERROR_CODE, 409));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> resource.advanceVehicleState(vehicleId));

        assertEquals(409, exception.getStatusCode());
        assertEquals(INVALID_VEHICLE_STATUS_ERROR_CODE, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());

        verify(service).advanceStage(vehicleId);
    }

    @Test
    void shouldThrowBusinessExceptionWhenStageTransitionIsInvalid() {
        final String vehicleId = "VEH-001";
        final String expectedMessage = String.format(INVALID_STAGE_TRANSITION_ERROR_MESSAGE, BODY, PAINT);

        when(service.advanceStage(vehicleId)).thenThrow(
                new BusinessException(expectedMessage, INVALID_STAGE_TRANSITION_ERROR_CODE, 409));

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> resource.advanceVehicleState(vehicleId));

        assertEquals(409, exception.getStatusCode());
        assertEquals(INVALID_STAGE_TRANSITION_ERROR_CODE, exception.getErrorCode());
        assertEquals(expectedMessage, exception.getMessage());

        verify(service).advanceStage(vehicleId);
    }

    private VehicleProductionResponse createResponse(final String vehicleId, final String orderId) {
        return new VehicleProductionResponse(vehicleId, orderId, CREATED, ProductionStatus.CREATED,
                OffsetDateTime.now(ZoneOffset.UTC), null);
    }

    private VehicleProductionView createView(final String vehicleId, final String orderId) {
        return new VehicleProductionView(vehicleId, orderId, CREATED, ProductionStatus.CREATED,
                OffsetDateTime.now(ZoneOffset.UTC), null);
    }
}