package com.smartfactory.quality.control.repositories;

import java.util.Collection;
import java.util.List;

import com.smartfactory.quality.entity.Defect;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

// Connection between the service layer and the database
@ApplicationScoped
public class DefectRepository implements PanacheRepositoryBase<Defect, String> {

    // Loads several defects at once
    public List<Defect> findByCodes(final Collection<String> codes) {
        if (codes.isEmpty()) {
            return List.of();
        }

        return list("code in ?1", codes);
    }
}