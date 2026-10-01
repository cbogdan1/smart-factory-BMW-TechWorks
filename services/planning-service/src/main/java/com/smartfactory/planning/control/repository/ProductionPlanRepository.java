package com.smartfactory.planning.control.repository;

import java.util.Optional;

import com.smartfactory.planning.entity.ProductionPlan;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductionPlanRepository implements PanacheRepositoryBase<ProductionPlan, String> {
    public Optional<ProductionPlan> findByVehicleId(final String vehicleId) {
        return find("vehicleId", vehicleId).firstResultOptional();
    }
}
