package com.smartfactory.agv.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.smartfactory.agv.control.exceptions.AssignedAgvNotFoundException;
import com.smartfactory.agv.control.exceptions.NoRouteException;
import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.control.repository.MissionRepository;
import com.smartfactory.agv.control.repository.ProcessedEventRepository;
import com.smartfactory.agv.control.service.AgvAssignmentService;
import com.smartfactory.agv.control.service.DeliveryMissionService;
import com.smartfactory.agv.control.service.RouteCalculatorService;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.agv.entity.models.DeliveryMission;
import com.smartfactory.common.dto.agv.AssignAgvResponse;
import com.smartfactory.common.dto.agv.RouteResponse;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DeliveryMissionServiceTest {

    @Inject
    DeliveryMissionService deliveryMissionService;

    @InjectMock
    AgvAssignmentService agvAssignmentService;

    @InjectMock
    RouteCalculatorService routeCalculatorService;

    @InjectMock
    MissionRepository missionRepository;

    @InjectMock
    ProcessedEventRepository processedEventRepository;

    @InjectMock
    AgvRepository agvRepository;

    @Inject
    Event<MaterialDeliveredPayload> deliverySuccessEvent;

    private MaterialRequestedPayload dummyPayload;

    @BeforeEach
    void setUp() {
        dummyPayload = new MaterialRequestedPayload("REQ-001", "VEH-001", "ORD-001", "STEEL", 50, ProductionStage.PAINT,
                "TARGET-NODE");
    }

    @Test
    void shouldReturnImmediatelyIfEventAlreadyProcessed() {
        // Arrange
        when(processedEventRepository.isEventProcessed("EVT-123")).thenReturn(true);

        // Act
        deliveryMissionService.executeDeliveryMission("EVT-123", "material-requested", dummyPayload);

        // Assert
        verify(agvAssignmentService, never()).assignAgv(any());
        verify(missionRepository, never()).persist(any(DeliveryMission.class));
    }

    @Test
    void shouldThrowExceptionWhenRouteIsEmpty() {
        // Arrange
        when(processedEventRepository.isEventProcessed(anyString())).thenReturn(false);

        final AssignAgvResponse assignRes = AssignAgvResponse.builder().agvId("AGV-001").build();
        when(agvAssignmentService.assignAgv(any())).thenReturn(assignRes);

        final RouteResponse routeRes = RouteResponse.builder().path(Collections.emptyList()).build();
        when(routeCalculatorService.calculateRoute(any())).thenReturn(routeRes);

        // Act & Assert
        final NoRouteException exception = assertThrows(NoRouteException.class, () -> {
            deliveryMissionService.executeDeliveryMission("EVT-123", "material-requested", dummyPayload);
        });

        assertEquals("No route available from WAREHOUSE-A to TARGET-NODE", exception.getMessage());

        assertEquals("NO_ROUTE_FOUND", exception.getErrorCode());

        assertEquals(422, exception.getStatusCode());
        verify(missionRepository, never()).persist(any(DeliveryMission.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldThrowExceptionWhenAssignedAgvNotFoundInDatabase() {
        // Arrange
        when(processedEventRepository.isEventProcessed(anyString())).thenReturn(false);

        final AssignAgvResponse assignRes = AssignAgvResponse.builder().agvId("AGV-001").build();
        when(agvAssignmentService.assignAgv(any())).thenReturn(assignRes);

        final RouteResponse routeRes = RouteResponse.builder().path(List.of("NODE-A", "NODE-B")).build();
        when(routeCalculatorService.calculateRoute(any())).thenReturn(routeRes);

        final PanacheQuery<Agv> mockQuery = mock(PanacheQuery.class);
        when(agvRepository.find(eq("agvId"), any(Object[].class))).thenReturn(mockQuery);
        when(mockQuery.firstResultOptional()).thenReturn(Optional.empty());

        // Act & Assert
        final AssignedAgvNotFoundException exception = assertThrows(AssignedAgvNotFoundException.class,
                () -> deliveryMissionService.executeDeliveryMission("EVT-123", "material-requested", dummyPayload));

        assertEquals("Assigned AGV not found: AGV-001", exception.getMessage());

        assertEquals("ASSIGNED_AGV_NOT_FOUND", exception.getErrorCode());

        assertEquals(404, exception.getStatusCode());
        verify(missionRepository, never()).persist(any(DeliveryMission.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldExecuteMissionSuccessfully() {
        // Arrange
        when(processedEventRepository.isEventProcessed(anyString())).thenReturn(false);

        final AssignAgvResponse assignRes = AssignAgvResponse.builder().agvId("AGV-001").build();
        when(agvAssignmentService.assignAgv(any())).thenReturn(assignRes);

        final RouteResponse routeRes =
                RouteResponse.builder().path(List.of("WAREHOUSE-A", "TARGET-NODE")).totalDistanceMeters(100).build();
        when(routeCalculatorService.calculateRoute(any())).thenReturn(routeRes);

        final Agv agv = new Agv();
        agv.setAgvId("AGV-001");

        final PanacheQuery<Agv> mockQuery = mock(PanacheQuery.class);
        when(agvRepository.find(eq("agvId"), any(Object[].class))).thenReturn(mockQuery);
        when(mockQuery.firstResultOptional()).thenReturn(Optional.of(agv));

        // Act
        deliveryMissionService.executeDeliveryMission("EVT-123", "material-requested", dummyPayload);

        // Assert
        verify(missionRepository, times(2)).persist(any(DeliveryMission.class));
        verify(processedEventRepository).markEventProcessed("EVT-123", "material-requested");
        verify(agvRepository, times(2)).persist(agv);
    }
}