package com.smartfactory.quality.control.repositories;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.quality.entity.Inspection;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InspectionRepository implements PanacheRepositoryBase<Inspection, String> {
    public long countReworks(final String vehicleId) {
        return count("vehicleId = ?1 and decision = ?2", vehicleId, Decision.REWORK);
    }
}
