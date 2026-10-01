package com.smartfactory.agv.control.repository;

import java.util.Optional;

import com.smartfactory.agv.entity.models.DeliveryMission;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MissionRepository implements PanacheRepository<DeliveryMission> {

    public Optional<DeliveryMission> findByMissionId(final String missionId) {
        return find("missionId", missionId).firstResultOptional();
    }
}