package com.smartfactory.assembly.control.mapper;

import java.time.Instant;
import java.util.UUID;

import com.smartfactory.assembly.entity.VehicleProduction;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.enums.StageMaterial;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.common.payloads.assembly_service.VehicleStageAdvancedPayload;

public class VehicleProductionPayloadMapper {
    private final static int quantity = 1;

    public static VehicleStageAdvancedPayload toStageAdvancePayload(final VehicleProduction vehicleProduction,
            final ProductionStage currentStage, final ProductionStage nextProductionStage) {
        return new VehicleStageAdvancedPayload(vehicleProduction.getVehicleId(), vehicleProduction.getOrderId(),
                currentStage, nextProductionStage, Instant.now());
    }

    public static MaterialRequestedPayload toMaterialRequestedPayload(final VehicleProduction vehicleProduction,
            final StageMaterial requestedMaterial) {
        final String requestId = "REQ-" + UUID.randomUUID();
        final String targetNode =
                vehicleProduction.getProductionLine() + "-" + vehicleProduction.getCurrentStage() + "-STATION";

        return new MaterialRequestedPayload(requestId, vehicleProduction.getVehicleId(), vehicleProduction.getOrderId(),
                requestedMaterial.name(), quantity, vehicleProduction.getCurrentStage(), targetNode);
    }

    public static VehicleAssembledPayload toAssembledPayload(final VehicleProduction vehicleProduction) {
        return new VehicleAssembledPayload(vehicleProduction.getVehicleId(), vehicleProduction.getOrderId(),
                vehicleProduction.getVehicleModel(), vehicleProduction.getProductionLine(),
                vehicleProduction.getCompletedAt().toInstant());
    }
}
