package com.smartfactory.assembly.control.mapper;

import static com.smartfactory.common.enums.ProductionStage.CREATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.smartfactory.assembly.entity.VehicleProduction;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.dto.assembly.VehicleProductionResponse;
import com.smartfactory.common.dto.assembly.VehicleProductionView;
import com.smartfactory.common.enums.ProductionStatus;
import org.junit.jupiter.api.Test;

class VehicleProductionMapperTest {

    @Test
    void shouldMapCreationRequestToEntityWithDefaultValues() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final String vehicleModel = "BMW_I4";
        final String productionLine = "LINE-1";

        final CreateVehicleProductionRequest request =
                new CreateVehicleProductionRequest(
                        vehicleId,
                        orderId,
                        vehicleModel,
                        productionLine);

        final VehicleProduction vehicleProduction =
                VehicleProductionMapper.fromRequest(request);

        assertEquals(vehicleId, vehicleProduction.getVehicleId());
        assertEquals(orderId, vehicleProduction.getOrderId());
        assertEquals(vehicleModel, vehicleProduction.getVehicleModel());
        assertEquals(productionLine, vehicleProduction.getProductionLine());
        assertEquals(CREATED, vehicleProduction.getCurrentStage());
        assertEquals(ProductionStatus.CREATED, vehicleProduction.getStatus());
        assertNull(vehicleProduction.getStartedAt());
        assertNull(vehicleProduction.getStartedEventId());
        assertNull(vehicleProduction.getCompletedAt());
    }

    @Test
    void shouldMapEntityToResponse() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final OffsetDateTime startedAt =
                OffsetDateTime.now(ZoneOffset.UTC);

        final OffsetDateTime completedAt =
                OffsetDateTime.now(ZoneOffset.UTC);

        final VehicleProduction vehicleProduction =
                createVehicleProduction(
                        vehicleId,
                        orderId,
                        startedAt,
                        completedAt);

        final VehicleProductionResponse response =
                VehicleProductionMapper.toResponse(vehicleProduction);

        assertEquals(vehicleId, response.vehicleId());
        assertEquals(orderId, response.orderId());
        assertEquals(CREATED, response.currentStage());
        assertEquals(ProductionStatus.CREATED, response.status());
        assertEquals(startedAt, response.startedAt());
        assertEquals(completedAt, response.completedAt());
    }

    @Test
    void shouldMapEntityToView() {
        final String vehicleId = "VEH-001";
        final String orderId = "ORD-001";
        final OffsetDateTime startedAt =
                OffsetDateTime.now(ZoneOffset.UTC);

        final OffsetDateTime completedAt =
                OffsetDateTime.now(ZoneOffset.UTC);

        final VehicleProduction vehicleProduction =
                createVehicleProduction(
                        vehicleId,
                        orderId,
                        startedAt,
                        completedAt);

        final VehicleProductionView view =
                VehicleProductionMapper.toView(vehicleProduction);

        assertEquals(vehicleId, view.vehicleId());
        assertEquals(orderId, view.orderId());
        assertEquals(CREATED, view.currentStage());
        assertEquals(ProductionStatus.CREATED, view.status());
        assertEquals(startedAt, view.startedAt());
        assertEquals(completedAt, view.completedAt());
    }

    private VehicleProduction createVehicleProduction(
            final String vehicleId,
            final String orderId,
            final OffsetDateTime startedAt,
            final OffsetDateTime completedAt) {

        final VehicleProduction vehicleProduction =
                new VehicleProduction();

        vehicleProduction.setVehicleId(vehicleId);
        vehicleProduction.setOrderId(orderId);
        vehicleProduction.setProductionLine("LINE-1");
        vehicleProduction.setCurrentStage(CREATED);
        vehicleProduction.setStatus(ProductionStatus.CREATED);
        vehicleProduction.setStartedAt(startedAt);
        vehicleProduction.setCompletedAt(completedAt);

        return vehicleProduction;
    }
}
