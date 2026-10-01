package com.smartfactory.dwh.control.repository;

import java.util.List;

import com.smartfactory.dwh.entity.VehicleTimelineEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VehicleTimelineRepository implements PanacheRepository<VehicleTimelineEntity> {

    public List<VehicleTimelineEntity> findByVehicleId(final String vehicleId) {
        return list("vehicleId = ?1 order by eventTimestamp asc", vehicleId);
    }

    public boolean existsByEventId(final String eventId) {
        if (eventId == null) {
            return false;
        }
        return find("eventId", eventId).firstResultOptional().isPresent();
    }
}
