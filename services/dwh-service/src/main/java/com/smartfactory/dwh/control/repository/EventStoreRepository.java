package com.smartfactory.dwh.control.repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.smartfactory.dwh.entity.EventStoreEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventStoreRepository implements PanacheRepository<EventStoreEntity> {

    public Optional<Instant> getLastUpdatedTimestamp() {

        return Optional.ofNullable(
                getEntityManager().createQuery("SELECT MAX(e.timestamp) FROM EventStoreEntity e", Instant.class)
                        .getSingleResult());
    }

    public boolean existsByEventId(final String eventId) {
        if (eventId == null || eventId.isBlank()) {
            return false;
        }
        return count("eventId", eventId) > 0;
    }

    public Map<String, Long> countEventsByType() {
        final List<Object[]> results = getEntityManager().createQuery("""
                    SELECT e.eventType, COUNT(*)
                    FROM EventStoreEntity e
                    GROUP BY e.eventType
                """, Object[].class).getResultList();
        final Map<String, Long> counts = new HashMap<>();
        for (final Object[] row : results) {
            counts.put((String) row[0], ((Number) row[1]).longValue());
        }
        return counts;
    }
}
