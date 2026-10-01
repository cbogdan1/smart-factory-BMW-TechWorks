package com.smartfactory.inventory.control;

import java.util.Optional;
import java.util.UUID;
import com.smartfactory.inventory.entity.Part;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PartRepository implements PanacheRepositoryBase<Part, UUID> {
    /**
        * Finds a part by its part code in the inventory system.
        *
        * @param partCode The part code of the part to find.
        * @return An Optional containing the found part, or empty if not found.
     */
    public Optional<Part> findByPartCode(final String partCode) {
        return find("partCode", partCode).firstResultOptional();
    }

    public Optional<Part> findByPartId(final String partId) {
        return find("partId", partId).firstResultOptional();
    }
}
