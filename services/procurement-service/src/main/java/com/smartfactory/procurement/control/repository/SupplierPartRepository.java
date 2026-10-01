package com.smartfactory.procurement.control.repository;

import java.util.List;
import java.util.Optional;

import com.smartfactory.procurement.entity.SupplierPart;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SupplierPartRepository implements PanacheRepository<SupplierPart> {
    public boolean existsBySupplierAndPart(final String supplierId, final String partId) {
        return count("supplier.supplierId = ?1 and partId = ?2", supplierId, partId) > 0;
    }

    /**
     * Returns suppliers for the given part, ranked by preference:
     * fastest lead time first, then highest supplier rating, then lowest unit cost.
     */

    public List<SupplierPart> findPreferredSuppliersByPartId(final String partId) {
        final Sort sort =
                Sort.by("leadTimeDays", Sort.Direction.Ascending).and("supplier.rating", Sort.Direction.Descending)
                        .and("unitCost", Sort.Direction.Ascending);
        return find("partId = ?1", sort, partId).list();
    }

    public Optional<SupplierPart> findBySupplierIdAndPartId(final String supplierId, final String partId) {
        return find("supplier.supplierId = :supplierId and partId = :partId",
                Parameters.with("supplierId", supplierId).and("partId", partId)).firstResultOptional();
    }
}
