package com.smartfactory.procurement.control.repository;

import java.util.Optional;

import com.smartfactory.procurement.entity.Supplier;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SupplierRepository implements PanacheRepositoryBase<Supplier, Long> {

    public Optional<Supplier> findBySupplierId(final String supplierId) {
        return find("supplierId", supplierId).firstResultOptional();
    }

    public Optional<Supplier> findByContactEmail(final String email) {
        return find("contactEmail", email).firstResultOptional();
    }

    public Optional<Supplier> findByName(final String name) {
        return find("name", name).firstResultOptional();
    }
}
