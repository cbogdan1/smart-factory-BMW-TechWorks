package com.smartfactory.procurement.control.repository;

import com.smartfactory.procurement.entity.ProcessedEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessedEventRepository implements PanacheRepositoryBase<ProcessedEvent, String> {
}
