package com.smartfactory.assembly.entity.mapper;

import static com.smartfactory.common.enums.ProductionStage.INTERIOR;
import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.enums.ProductionStage.POWERTRAIN;
import static com.smartfactory.common.enums.StageMaterial.BATTERY_PACK;
import static com.smartfactory.common.enums.StageMaterial.PAINT_KIT;
import static com.smartfactory.common.enums.StageMaterial.SEAT_SET;

import java.util.Map;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.enums.StageMaterial;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StageMaterialMapper {
    static Map<ProductionStage, StageMaterial> REQUIRED_MATERIAL =
            Map.of(PAINT, PAINT_KIT, INTERIOR, SEAT_SET, POWERTRAIN, BATTERY_PACK);

    public static StageMaterial getMaterialFor(final ProductionStage productionStage) {
        return REQUIRED_MATERIAL.getOrDefault(productionStage, null);
    }
}
