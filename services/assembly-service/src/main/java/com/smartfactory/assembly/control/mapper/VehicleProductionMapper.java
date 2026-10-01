package com.smartfactory.assembly.control.mapper;

import com.smartfactory.assembly.entity.VehicleProduction;
import com.smartfactory.common.dto.assembly.CreateVehicleProductionRequest;
import com.smartfactory.common.dto.assembly.VehicleProductionResponse;
import com.smartfactory.common.dto.assembly.VehicleProductionView;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.enums.ProductionStatus;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VehicleProductionMapper {

    public static VehicleProduction fromRequest(final CreateVehicleProductionRequest request) {
        final VehicleProduction vehicleProduction = new VehicleProduction();

        vehicleProduction.setVehicleId(request.vehicleId());
        vehicleProduction.setOrderId(request.orderId());
        vehicleProduction.setVehicleModel(request.vehicleModel());
        vehicleProduction.setProductionLine(request.productionLine());
        vehicleProduction.setCurrentStage(ProductionStage.CREATED);
        vehicleProduction.setStatus(ProductionStatus.CREATED);
        vehicleProduction.setStartedAt(null);
        vehicleProduction.setStartedEventId(null);
        vehicleProduction.setCompletedAt(null);

        return vehicleProduction;
    }

    public static VehicleProductionResponse toResponse(final VehicleProduction vehicleProduction) {
        return new VehicleProductionResponse(
                vehicleProduction.getVehicleId(),
                vehicleProduction.getOrderId(),
                vehicleProduction.getCurrentStage(),
                vehicleProduction.getStatus(),
                vehicleProduction.getStartedAt(),
                vehicleProduction.getCompletedAt());
    }

    public static VehicleProductionView toView(final VehicleProduction vehicleProduction) {
        return new VehicleProductionView(
                vehicleProduction.getVehicleId(),
                vehicleProduction.getOrderId(),
                vehicleProduction.getCurrentStage(),
                vehicleProduction.getStatus(),
                vehicleProduction.getStartedAt(),
                vehicleProduction.getCompletedAt());
    }
}
