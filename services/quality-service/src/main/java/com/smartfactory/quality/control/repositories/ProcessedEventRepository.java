package com.smartfactory.quality.control.repositories;

import com.smartfactory.quality.entity.ProcessedEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessedEventRepository implements PanacheRepositoryBase<ProcessedEvent, String> {
}