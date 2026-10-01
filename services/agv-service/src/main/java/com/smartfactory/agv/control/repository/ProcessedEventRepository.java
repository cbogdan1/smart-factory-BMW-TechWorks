package com.smartfactory.agv.control.repository;

import java.time.Instant;

import com.smartfactory.agv.entity.models.ProcessedEvent;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProcessedEventRepository implements PanacheRepositoryBase<ProcessedEvent, String> {

    public boolean isEventProcessed(final String eventId) {
        return findByIdOptional(eventId).isPresent();
    }

    public void markEventProcessed(final String eventId, final String eventType) {
        final ProcessedEvent event =
                ProcessedEvent.builder().eventId(eventId).eventType(eventType).processedAt(Instant.now()).build();

        persist(event);
    }
}