package com.smartfactory.agv.control.repository;

import java.util.Optional;

import com.smartfactory.agv.entity.models.Edge;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EdgeRepository implements PanacheRepositoryBase<Edge, Long> {
    public Optional<Edge> findByEdgeId(final String edgeId) {
        return find("edgeId", edgeId).firstResultOptional();
    }
}