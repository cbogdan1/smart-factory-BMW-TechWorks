package com.smartfactory.inventory.control;

import java.util.Optional;
import com.smartfactory.inventory.entity.InventoryStock;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * <p>
 *
 * @ ApplicationScoped because we need one instance of this repository/whole lifetime of the application
 * </p>
 */
@ApplicationScoped
public class InventoryStockRepository implements PanacheRepository<InventoryStock> {
    /**
     * Find a stock record by its {@code part_id}
     *
     * @param partId id of the part for which we want to retrieve the record
     * @return {@code Optional(InventoryStock)} - it may or may not find the requested object, so null is handled by using optional
     */
    public Optional<InventoryStock> findByPartId(final String partId) {
        return find("part.partId", partId).firstResultOptional();
    }
}
