package com.smartfactory.agv.control.repository;

import java.util.List;
import java.util.Optional;

import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.common.enums.AgvStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgvRepository implements PanacheRepository<Agv> {

    public Optional<Agv> findByAgvId(final String agvId) {

        return find("agvId", agvId).firstResultOptional();
    }

    public boolean existsByAgvId(final String agvId) {

        return count("agvId", agvId) > 0;
    }

    public List<Agv> findAvailableAgvs() {
        return list("status", AgvStatus.AVAILABLE);
    }
}