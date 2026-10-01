package com.smartfactory.dwh.control.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import com.smartfactory.common.dto.dwh.EventViewResponse;
import com.smartfactory.common.event.DomainEvent;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.dwh.control.repository.EventStoreRepository;
import com.smartfactory.dwh.entity.EventStoreEntity;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class EventStoreServiceTest {

    @Inject
    EventStoreService eventStoreService;

    @InjectMock
    EventStoreRepository eventStoreRepository;

    private DomainEvent<String> sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = new DomainEvent<>("evt-123", "ORDER_CREATED", "OrderService", "ord-111", "{\"orderId\": 111}");
    }

    @Test
    void shouldSaveEventSuccessfully() {
        // Arrange: Mock repository query to simulate no existing event in the database
        final PanacheQuery<EventStoreEntity> queryMock = Mockito.mock(PanacheQuery.class);
        when(eventStoreRepository.find("eventId", sampleEvent.eventId())).thenReturn(queryMock);
        when(queryMock.firstResult()).thenReturn(null);

        // Act
        final boolean isSaved = eventStoreService.saveEvent(sampleEvent);

        // Assert
        assertTrue(isSaved);
        verify(eventStoreRepository, Mockito.times(1)).persist((EventStoreEntity) any());
    }

    @Test
    void shouldIgnoreDuplicateEvent() {
        // Arrange: Mock repository query to simulate an existing event (duplicate)
        final PanacheQuery<EventStoreEntity> queryMock = Mockito.mock(PanacheQuery.class);
        when(eventStoreRepository.find("eventId", sampleEvent.eventId())).thenReturn(queryMock);
        when(queryMock.firstResult()).thenReturn(new EventStoreEntity());

        // Act
        final boolean isDuplicateSaved = eventStoreService.saveEvent(sampleEvent);

        // Assert
        Assertions.assertFalse(isDuplicateSaved);
        verify(eventStoreRepository, Mockito.never()).persist((EventStoreEntity) any());
    }

    @Test
    void shouldRetrieveEventsNewestToOldest() {

        //Arrange: create old and new entity and set listAll() behavior
        final EventStoreEntity oldEvent = new EventStoreEntity();
        oldEvent.setEventId("EVT-OLD");
        oldEvent.setEventType("order-created");
        oldEvent.setSourceService("order-service");
        oldEvent.setCorrelationId("VEH-001");
        oldEvent.setTimestamp(Instant.parse("2026-01-01T10:00:00Z"));

        final EventStoreEntity newEvent = new EventStoreEntity();
        newEvent.setEventId("EVT-NEW");
        newEvent.setEventType("order-created");
        newEvent.setSourceService("order-service");
        newEvent.setCorrelationId("VEH-001");
        newEvent.setTimestamp(Instant.parse("2026-01-02T10:00:00Z"));

        when(eventStoreRepository.listAll()).thenReturn(List.of(oldEvent, newEvent));

        //Act : call getAllEvents method from service
        final List<EventViewResponse> result = eventStoreService.getAllEvents();

        //Assert : check the order of the events
        //Expected : New event first
        assertEquals("EVT-NEW", result.get(0).eventId());
        assertEquals("EVT-OLD", result.get(1).eventId());

    }

    @Test
    void shouldReturnEmptyListWhenNoEventsExist() {
        //Arrange: Make sure repository is empty
        when(eventStoreRepository.listAll()).thenReturn(List.of());

        //Act : call service method for testing
        final List<EventViewResponse> result = eventStoreService.getAllEvents();

        //Assert : check that the behavior is the one expected
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails() {
        //Arrange : make sure that there is an error
        when(eventStoreRepository.listAll()).thenThrow(new RuntimeException("DB error"));
        
        //Act and Assert : check that when calling the service method, it throws an exception
        final BusinessException exception =
                assertThrows(BusinessException.class, () -> eventStoreService.getAllEvents());
        assertEquals("Failed to retrieve events from store", exception.getMessage());
        assertEquals("EVENT_STORE_RETRIEVAL_FAILED", exception.getErrorCode());
        assertEquals(500, exception.getStatusCode());
    }
}
