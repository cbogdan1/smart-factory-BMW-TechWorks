package com.smartfactory.assembly.control.repositories;

import java.util.Optional;

import com.smartfactory.assembly.entity.VehicleProduction;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleProductionRepository
        implements PanacheRepositoryBase<VehicleProduction, String> {

    public Optional<VehicleProduction> findByStartedEventId(final String eventId) {
        return find("startedEventId", eventId).firstResultOptional();
    }

    public Optional<VehicleProduction> findByVehicleId(final String vehicleId) {
        return find("vehicleId", vehicleId).firstResultOptional();
    }
}
