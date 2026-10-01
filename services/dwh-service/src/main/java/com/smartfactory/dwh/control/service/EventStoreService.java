package com.smartfactory.dwh.control.service;

import static com.smartfactory.common.exception.DataWarehouseErrorCodes.EVENT_STORE_RETRIEVAL_FAILED;

import java.util.Comparator;
import java.util.List;

import com.smartfactory.common.dto.dwh.EventViewResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.mapper.EventStoreMapper;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import com.smartfactory.dwh.entity.EventStoreEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventStoreService {

    private static final Logger LOG = Logger.getLogger(EventStoreService.class);

    private final EventStoreRepository eventStoreRepository;

    public EventStoreService(final EventStoreRepository eventStoreRepository) {
        this.eventStoreRepository = eventStoreRepository;
    }

    @Transactional
    public boolean saveEvent(final DomainEvent<?> event) {
        if (event == null || event.eventId() == null) {
            LOG.warn("[EVENT_STORE] Received null event or missing eventId");
            return false;
        }

        final String eventId = event.eventId();
        final String eventType = event.eventType();

        LOG.infof("[%s] Processing event store persistence for eventId=%s", eventType, eventId);

        // Check for event duplication by eventId
        final EventStoreEntity existingEvent = eventStoreRepository.find("eventId", eventId).firstResult();

        if (existingEvent != null) {
            LOG.warnf("[%s] Duplicate event found with ID=%s. Skipping persistence.", eventType, eventId);
            return false;
        }

        // Map and persist the event
        final EventStoreEntity entity = EventStoreMapper.toEntity(event);
        eventStoreRepository.persist(entity);

        LOG.infof("[%s] Successfully persisted event for eventId=%s", eventType, eventId);
        return true;
    }

    public List<EventViewResponse> getAllEvents() {
        try {
            final List<EventStoreEntity> events = eventStoreRepository.listAll();

            if (events == null) {
                return List.of();
            }

            return events.stream().map(EventStoreMapper::toView)
                    .sorted(Comparator.comparing(EventViewResponse::timestamp).reversed()).toList();
        } catch (final Exception e) {
            LOG.error("[EVENT_STORE] Failed to retrieve events", e);
            throw new BusinessException("Failed to retrieve events from store", EVENT_STORE_RETRIEVAL_FAILED, 500);
        }
    }
}
