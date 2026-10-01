package com.smartfactory.assembly.control.repositories;

import com.smartfactory.assembly.entity.EventProcessed;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class EventsProcessedRepository
        implements PanacheRepositoryBase<EventProcessed, String> {

    public boolean isEventProcessed(final String eventId) {
        return find("eventId", eventId).firstResultOptional().isPresent();
    }
}
