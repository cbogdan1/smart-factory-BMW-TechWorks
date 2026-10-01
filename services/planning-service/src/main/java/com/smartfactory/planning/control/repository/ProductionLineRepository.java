package com.smartfactory.planning.control.repository;

import java.util.Optional;

import com.smartfactory.planning.entity.ProductionLine;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

@ApplicationScoped
public class ProductionLineRepository implements PanacheRepositoryBase<ProductionLine, String> {
    @Inject
    EntityManager entityManager;

    public Optional<ProductionLine> findLeastLoadedLineForUpdate() {
        return find("enabled = true order by currentLoad asc").firstResultOptional()
                .map(line -> entityManager.find(ProductionLine.class, line.getLineId(),
                        LockModeType.PESSIMISTIC_WRITE));
    }

    public Optional<ProductionLine> findByLineIdForUpdate(final String lineId) {
        return findByIdOptional(lineId).map(
                line -> entityManager.find(ProductionLine.class, line.getLineId(), LockModeType.PESSIMISTIC_WRITE));
    }

    public void incrementLoad(final ProductionLine productionLine) {
        productionLine.setCurrentLoad(productionLine.getCurrentLoad() + 1);
    }

    public void decrementLoad(final ProductionLine productionLine) {
        if (productionLine.getCurrentLoad() > 0) {
            productionLine.setCurrentLoad(productionLine.getCurrentLoad() - 1);
        }
    }
}