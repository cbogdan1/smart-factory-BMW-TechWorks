package com.smartfactory.agv.control.service;

import java.time.Instant;
import java.util.UUID;

import com.smartfactory.agv.boundary.kafka.MaterialDeliveredProducer;
import com.smartfactory.agv.control.exceptions.AssignedAgvNotFoundException;
import com.smartfactory.agv.control.exceptions.NoRouteException;
import com.smartfactory.agv.control.repository.AgvRepository;
import com.smartfactory.agv.control.repository.MissionRepository;
import com.smartfactory.agv.control.repository.ProcessedEventRepository;
import com.smartfactory.agv.entity.models.Agv;
import com.smartfactory.agv.entity.models.DeliveryMission;
import com.smartfactory.common.dto.agv.AssignAgvRequest;
import com.smartfactory.common.dto.agv.AssignAgvResponse;
import com.smartfactory.common.dto.agv.CreateCalculateRouteRequest;
import com.smartfactory.common.dto.agv.RouteResponse;
import com.smartfactory.common.enums.AgvStatus;
import com.smartfactory.common.enums.MissionStatus;
import com.smartfactory.common.payloads.agv_service.MaterialDeliveredPayload;
import com.smartfactory.common.payloads.assembly_service.MaterialRequestedPayload;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeliveryMissionService {

    @Inject
    AgvAssignmentService agvAssignmentService;

    @Inject
    RouteCalculatorService routeCalculatorService;

    @Inject
    MaterialDeliveredProducer producer;

    @Inject
    MissionRepository missionRepository;

    @Inject
    ProcessedEventRepository processedEventRepository;

    @Inject
    AgvRepository agvRepository;

    @Inject
    Event<MaterialDeliveredPayload> deliverySuccessEvent;

    @Transactional
    public void executeDeliveryMission(final String eventId, final String eventType,
            final MaterialRequestedPayload payload) {

        if (processedEventRepository.isEventProcessed(eventId)) {
            return;
        }

        final String sourceNodeId = "WAREHOUSE-A";
        final String targetNodeId = payload.targetNode();

        final AssignAgvResponse assignedAgv = agvAssignmentService.assignAgv(
                AssignAgvRequest.builder().sourceNodeId(sourceNodeId).targetNodeId(targetNodeId).build());

        final RouteResponse route = routeCalculatorService.calculateRoute(
                CreateCalculateRouteRequest.builder().sourceNodeId(sourceNodeId).targetNodeId(targetNodeId).build());

        if (route.getPath() == null || route.getPath().isEmpty()) {
            throw new NoRouteException(sourceNodeId, targetNodeId);
        }

        final Agv agv = agvRepository.find("agvId", assignedAgv.getAgvId()).firstResultOptional()
                .orElseThrow(() -> new AssignedAgvNotFoundException(assignedAgv.getAgvId()));
        final String missionId = "MIS-" + UUID.randomUUID().toString().substring(0, 5);

        final DeliveryMission mission = DeliveryMission.builder().missionId(missionId).materialRequestEventId(eventId)
                .requestId(payload.requestId()).vehicleId(payload.vehicleId()).orderId(payload.orderId())
                .material(payload.material()).quantity(payload.quantity()).sourceNodeId(sourceNodeId)
                .targetNodeId(targetNodeId).plannedRoute(String.join("->", route.getPath()))
                .distanceMeters(route.getTotalDistanceMeters()).status(MissionStatus.ASSIGNED)
                .createdDate(Instant.now()).build();

        missionRepository.persist(mission);
        processedEventRepository.markEventProcessed(eventId, eventType);

        agv.setStatus(AgvStatus.BUSY);
        agvRepository.persist(agv);

        completeMissionForDemo(mission, agv);
    }

    private void completeMissionForDemo(final DeliveryMission mission, final Agv agv) {
        mission.setStatus(MissionStatus.COMPLETED);
        mission.setCompletedDate(Instant.now());
        missionRepository.persist(mission);

        agv.setStatus(AgvStatus.AVAILABLE);
        agvRepository.persist(agv);

        final MaterialDeliveredPayload deliveredPayload =
                new MaterialDeliveredPayload(mission.getMissionId(), agv.getAgvId(), mission.getVehicleId(),
                        mission.getMaterial(), mission.getQuantity(), mission.getTargetNodeId(), Instant.now());

        deliverySuccessEvent.fire(deliveredPayload);
    }

    public void onDeliveryMissionCommitted(
            @Observes(during = TransactionPhase.AFTER_SUCCESS) final MaterialDeliveredPayload payload) {
        producer.publishMaterialDelivered(payload);
    }
}