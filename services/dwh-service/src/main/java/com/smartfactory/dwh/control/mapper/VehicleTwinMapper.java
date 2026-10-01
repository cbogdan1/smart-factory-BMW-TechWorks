package com.smartfactory.dwh.control.mapper;

import com.smartfactory.common.dto.dwh.VehicleTwinResponse;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VehicleTwinMapper {
    public static VehicleTwinResponse toResponse(final VehicleTwinEntity entity) {
        if (entity == null) {
            return null;
        }
        return VehicleTwinResponse.builder().vehicleId(entity.getVehicleId()).orderId(entity.getOrderId())
                .vehicleModel(entity.getVehicleModel()).status(entity.getStatus())
                .currentStage(entity.getCurrentStage()).currentLocation(entity.getCurrentLocation())
                .qualityStatus(entity.getQualityStatus()).reworkCount(entity.getReworkCount())
                .lastUpdated(entity.getLastUpdated()).build();
    }
}
