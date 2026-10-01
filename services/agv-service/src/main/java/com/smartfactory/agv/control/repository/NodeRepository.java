package com.smartfactory.agv.control.repository;

import java.util.Optional;

import com.smartfactory.agv.entity.models.Node;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NodeRepository implements PanacheRepository<Node> {

    public Optional<Node> findByName(final String name) {
        return find("name", name).firstResultOptional();
    }

    public Optional<Node> findByNodeId(final String nodeId) {
        return find("nodeId", nodeId).firstResultOptional();
    }
}