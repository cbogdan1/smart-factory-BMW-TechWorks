package com.smartfactory.dwh.control.repository;

import com.smartfactory.common.enums.OrderStatus;
import com.smartfactory.dwh.entity.VehicleTwinEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleTwinRepository implements PanacheRepositoryBase<VehicleTwinEntity, String> {

    public VehicleTwinEntity findByVehicleId(final String vehicleId) {
        return findById(vehicleId);
    }

    public long countVehiclesInProduction() {
        return count("status", OrderStatus.IN_PRODUCTION);
    }
}
