package com.smartfactory.quality.control.services;

import static com.smartfactory.common.exception.QualityServiceExceptions.DEFECT_ALREADY_LINKED_ERROR_CODE;
import static com.smartfactory.common.exception.QualityServiceExceptions.DEFECT_ALREADY_LINKED_ERROR_MESSAGE;
import static com.smartfactory.common.exception.QualityServiceExceptions.INSPECTION_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.QualityServiceExceptions.INSPECTION_NOT_FOUND_ERROR_MESSAGE;
import static com.smartfactory.common.exception.QualityServiceExceptions.INVALID_DEFECT_CODE_ERROR_CODE;
import static com.smartfactory.common.exception.QualityServiceExceptions.INVALID_DEFECT_CODE_ERROR_MESSAGE;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.smartfactory.common.Topics;
import com.smartfactory.common.dto.quality.AddDefectToInspectionRequest;
import com.smartfactory.common.dto.quality.CreateInspectionRequest;
import com.smartfactory.common.dto.quality.InspectionDecisionResponse;
import com.smartfactory.common.dto.quality.InspectionDefectResponse;
import com.smartfactory.common.dto.quality.InspectionDefectView;
import com.smartfactory.common.dto.quality.InspectionResponse;
import com.smartfactory.common.dto.quality.InspectionScoreResponse;
import com.smartfactory.common.dto.quality.InspectionView;
import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.InspectionStatus;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.common.payloads.assembly_service.VehicleAssembledPayload;
import com.smartfactory.common.payloads.quality_service.QualityApprovedPayload;
import com.smartfactory.common.payloads.quality_service.QualityFailedPayload;
import com.smartfactory.common.payloads.quality_service.QualityReworkRequiredPayload;
import com.smartfactory.quality.control.publisher.QualityEventPublisher;
import com.smartfactory.quality.control.repositories.DefectRepository;
import com.smartfactory.quality.control.repositories.InspectionDefectRepository;
import com.smartfactory.quality.control.repositories.InspectionRepository;
import com.smartfactory.quality.control.repositories.ProcessedEventRepository;
import com.smartfactory.quality.control.utils.InspectionDefectMapper;
import com.smartfactory.quality.control.utils.InspectionMapper;
import com.smartfactory.quality.control.utils.InspectionViewMapper;
import com.smartfactory.quality.entity.Defect;
import com.smartfactory.quality.entity.Inspection;
import com.smartfactory.quality.entity.InspectionDefect;
import com.smartfactory.quality.entity.ProcessedEvent;
import com.smartfactory.quality.entity.engine.DefectSeeder;
import com.smartfactory.quality.entity.engine.InspectionDecisionEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InspectionService {

    private static final int MAX_SCORE = 100;
    private static final int MIN_SCORE = 0;
    private static final String ROBOT_INSPECTOR = "QA-ROBOT-01";
    private static final Logger LOG = LoggerFactory.getLogger(QualityEventPublisher.class);
    private static final int MAX_REWORK_ATTEMPTS = 3;

    @Inject
    InspectionRepository inspectionRepository;
    @Inject
    InspectionDefectRepository inspectionDefectRepository;
    @Inject
    DefectRepository defectRepository;
    @Inject
    InspectionDecisionEngine inspectionDecisionEngine;
    @Inject
    Event<QualityApprovedPayload> approvedEvent;
    @Inject
    Event<QualityReworkRequiredPayload> reworkRequiredEvent;
    @Inject
    Event<QualityFailedPayload> failedEvent;
    @Inject
    ProcessedEventRepository processedEventRepository;
    @Inject
    DefectSeeder defectSeeder;

    @Transactional
    public void performInspection(final String eventId, final VehicleAssembledPayload payload) {

        if (processedEventRepository.findByIdOptional(eventId).isPresent()) {
            LOG.info("Event already processed: eventId={}", eventId);
            return;
        }

        markProcessed(eventId);

        final long reworkCount = inspectionRepository.countReworks(payload.vehicleId());

        final Inspection inspection = new Inspection();
        inspection.setInspectionId("INSP-" + UUID.randomUUID());
        inspection.setVehicleId(payload.vehicleId());
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setStatus(InspectionStatus.PENDING);
        inspection.setInspector(ROBOT_INSPECTOR);
        inspectionRepository.persist(inspection);

        seedDefects(inspection.getInspectionId(), reworkCount);

        calculateScore(inspection.getInspectionId());
    }

    private void markProcessed(final String eventId) {
        final ProcessedEvent processedEvent = new ProcessedEvent();
        processedEvent.setEventId(eventId);
        processedEvent.setEventType(Topics.VEHICLE_ASSEMBLED);
        processedEvent.setProcessedAt(Instant.now());

        processedEventRepository.persist(processedEvent);
    }

    private void seedDefects(final String inspectionId, final long reworkCount) {
        for (final String defectCode : defectSeeder.seedFor(reworkCount, defectRepository.listAll())) {
            final AddDefectToInspectionRequest request = new AddDefectToInspectionRequest();
            request.setDefectCode(defectCode);
            request.setComment(defectSeeder.commentFor(defectCode));

            inspectionDefectRepository.persist(InspectionDefectMapper.toInspectionDefect(inspectionId, request));
        }
    }

    @Transactional
    public InspectionDefectResponse addDefectToInspection(final String inspectionId,
            final AddDefectToInspectionRequest request) {

        findInspectionOrThrow(inspectionId);

        validateDefectByCode(request.getDefectCode());

        checkDefectAlreadyLinked(inspectionId, request.getDefectCode());

        final InspectionDefect inspectionDefect = InspectionDefectMapper.toInspectionDefect(inspectionId, request);

        inspectionDefectRepository.persist(inspectionDefect);

        recomputeScore(inspectionId);

        return InspectionDefectMapper.toResponse(inspectionDefect);
    }

    @Transactional
    public InspectionResponse createInspection(final CreateInspectionRequest request) {
        final Inspection inspection = InspectionMapper.toInspection(request);
        inspectionRepository.persist(inspection);

        return InspectionMapper.toResponse(inspection);
    }

    @Transactional
    public InspectionScoreResponse calculateScore(final String inspectionId) {
        final Inspection inspection = recomputeScore(inspectionId);

        final List<InspectionDefect> inspectionDefects = inspectionDefectRepository.findByInspectionId(inspectionId);

        publishDecision(inspection, inspectionDefects, findDefectsByCode(inspectionDefects).values());

        return InspectionMapper.toScoreResponse(inspection, inspectionDefects.size());
    }

    private Inspection recomputeScore(final String inspectionId) {
        final Inspection inspection = findInspectionOrThrow(inspectionId);
        final List<InspectionDefect> inspectionDefects = inspectionDefectRepository.findByInspectionId(inspectionId);
        final Map<String, Defect> defectsByCode = findDefectsByCode(inspectionDefects);

        inspection.setScore(clampScore(MAX_SCORE - sumPenaltyPoints(inspectionDefects, defectsByCode)));
        inspection.setCalculatedAt(LocalDateTime.now());
        applyDecision(inspection, defectsByCode);
        inspectionRepository.persist(inspection);

        return inspection;
    }

    public InspectionDecisionResponse findDecisionById(final String inspectionId) {
        final Inspection inspection = findInspectionOrThrow(inspectionId);

        return InspectionMapper.toDecisionResponse(inspection);
    }

    private void applyDecision(final Inspection inspection, final Map<String, Defect> defectsByCode) {
        final Decision decision = capReworks(inspection);
        inspection.setDecision(decision);
        inspection.setStatus(toStatus(decision));
        inspection.setDecidedAt(LocalDateTime.now());

        if (decision == Decision.REWORK) {
            inspection.setTargetStage(inspectionDecisionEngine.earliestProductionStage(defectsByCode.values()));
        } else {
            inspection.setTargetStage(null);
        }
    }

    private Decision capReworks(final Inspection inspection) {
        final Decision decision = inspectionDecisionEngine.decide(inspection.getScore());

        if (decision != Decision.REWORK) {
            return decision;
        }

        if (inspectionRepository.countReworks(inspection.getVehicleId()) < MAX_REWORK_ATTEMPTS) {
            return decision;
        }

        LOG.info("Rework budget spent after {} attempts, failing vehicle instead: vehicleId={}", MAX_REWORK_ATTEMPTS,
                inspection.getVehicleId());

        return Decision.FAIL;
    }

    private InspectionStatus toStatus(final Decision decision) {
        if (decision == Decision.PASS) {
            return InspectionStatus.APPROVED;
        }
        if (decision == Decision.REWORK) {
            return InspectionStatus.REWORK_REQUIRED;
        }
        return InspectionStatus.FAILED;
    }

    private List<String> toDefectCodes(final List<InspectionDefect> inspectionDefects) {
        final List<String> defectCodes = new ArrayList<>();
        for (final InspectionDefect inspectionDefect : inspectionDefects) {
            defectCodes.add(inspectionDefect.getDefectCode());
        }

        return defectCodes;
    }

    private void publishDecision(final Inspection inspection, final List<InspectionDefect> inspectionDefects,
            final Collection<Defect> defects) {
        final Instant decidedAt = inspection.getDecidedAt().toInstant(ZoneOffset.UTC);

        if (inspection.getDecision() == Decision.PASS) {
            approvedEvent.fire(new QualityApprovedPayload(inspection.getInspectionId(), inspection.getVehicleId(),
                    inspection.getScore(), decidedAt));
            return;
        }

        if (inspection.getDecision() == Decision.REWORK) {
            final String reason = inspectionDecisionEngine.determineReason(defects, inspection.getTargetStage());
            reworkRequiredEvent.fire(
                    new QualityReworkRequiredPayload(inspection.getInspectionId(), inspection.getVehicleId(),
                            inspection.getScore(), inspection.getTargetStage(), reason, decidedAt));
            return;
        }

        final List<String> defectCodes = toDefectCodes(inspectionDefects);
        failedEvent.fire(
                new QualityFailedPayload(inspection.getInspectionId(), inspection.getVehicleId(), inspection.getScore(),
                        defectCodes, decidedAt));
    }

    // three helper methods for calculateScore
    private int sumPenaltyPoints(final List<InspectionDefect> inspectionDefects,
            final Map<String, Defect> defectsByCode) {
        if (inspectionDefects == null || inspectionDefects.isEmpty()) {
            return 0;
        }
        int totalPenaltyPoints = 0;
        for (final InspectionDefect inspectionDefect : inspectionDefects) {
            final Defect defect = defectsByCode.get(inspectionDefect.getDefectCode());
            if (defect != null && defect.getPenaltyPoints() != null) {
                totalPenaltyPoints += defect.getPenaltyPoints();
            }
        }
        return totalPenaltyPoints;
    }

    private int clampScore(final int rawScore) {
        return Math.max(MIN_SCORE, rawScore);
    }

    private Inspection findInspectionOrThrow(final String inspectionId) {
        final Inspection inspection = inspectionRepository.findById(inspectionId);
        if (inspection == null) {
            throw new BusinessException(String.format(INSPECTION_NOT_FOUND_ERROR_MESSAGE, inspectionId),
                    INSPECTION_NOT_FOUND_ERROR_CODE, 404);
        }
        return inspection;
    }

    public InspectionView findById(final String inspectionId) {
        final Inspection inspection = findInspectionOrThrow(inspectionId);

        final List<InspectionDefect> inspectionDefects = inspectionDefectRepository.findByInspectionId(inspectionId);

        return InspectionViewMapper.toView(inspection, toDefectViews(inspectionDefects));
    }

    public List<InspectionResponse> findAll() {
        final List<Inspection> inspections = inspectionRepository.listAll();
        final List<InspectionResponse> responses = new ArrayList<>();

        for (final Inspection inspection : inspections) {
            responses.add(InspectionMapper.toResponse(inspection));
        }

        return responses;
    }

    public List<InspectionView> history(final String vehicleId, final Decision decision) {

        final List<Inspection> inspections;

        if (vehicleId != null && decision != null) {
            inspections =
                    inspectionRepository.list("vehicleId = ?1 and decision = ?2 order by inspectionDate", vehicleId,
                            decision);
        } else if (vehicleId != null) {
            inspections = inspectionRepository.list("vehicleId = ?1 order by inspectionDate", vehicleId);
        } else if (decision != null) {
            inspections = inspectionRepository.list("decision = ?1 order by inspectionDate", decision);
        } else {
            inspections = inspectionRepository.list("order by inspectionDate");
        }

        final List<InspectionView> views = new ArrayList<>();
        for (final Inspection inspection : inspections) {
            final List<InspectionDefect> inspectionDefects =
                    inspectionDefectRepository.findByInspectionId(inspection.getInspectionId());
            views.add(InspectionViewMapper.toView(inspection, toDefectViews(inspectionDefects)));
        }
        return views;
    }

    private List<InspectionDefectView> toDefectViews(final List<InspectionDefect> inspectionDefects) {
        final Map<String, Defect> defectsByCode = findDefectsByCode(inspectionDefects);
        final List<InspectionDefectView> defectViews = new ArrayList<>();

        for (final InspectionDefect inspectionDefect : inspectionDefects) {
            final Defect defect = defectsByCode.get(inspectionDefect.getDefectCode());
            defectViews.add(InspectionViewMapper.toDefectView(inspectionDefect, defect));
        }

        return defectViews;
    }

    private Map<String, Defect> findDefectsByCode(final List<InspectionDefect> inspectionDefects) {
        if (inspectionDefects == null || inspectionDefects.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<String, Defect> defectsByCode = new HashMap<>();
        final List<Defect> defects = defectRepository.findByCodes(toDefectCodes(inspectionDefects));
        if (defects == null || defects.isEmpty()) {
            return defectsByCode;
        }
        for (final Defect defect : defects) {
            defectsByCode.put(defect.getCode(), defect);
        }
        return defectsByCode;
    }

    private void validateDefectByCode(final String defectCode) {
        final Defect defect = defectRepository.findById(defectCode);
        if (defect == null) {
            throw new BusinessException(String.format(INVALID_DEFECT_CODE_ERROR_MESSAGE, defectCode),
                    INVALID_DEFECT_CODE_ERROR_CODE, 400);
        }
    }

    private void checkDefectAlreadyLinked(final String inspectionId, final String defectCode) {
        final boolean alreadyLinked =
                inspectionDefectRepository.existsByInspectionIdAndDefectCode(inspectionId, defectCode);
        if (alreadyLinked) {
            throw new BusinessException(String.format(DEFECT_ALREADY_LINKED_ERROR_MESSAGE, defectCode, inspectionId),
                    DEFECT_ALREADY_LINKED_ERROR_CODE, 409);
        }
    }
}