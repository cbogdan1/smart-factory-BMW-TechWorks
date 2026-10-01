package com.smartfactory.quality.control.services;

import static com.smartfactory.common.exception.QualityServiceExceptions.DEFECT_ALREADY_EXISTS_ERROR_CODE;
import static com.smartfactory.common.exception.QualityServiceExceptions.DEFECT_ALREADY_EXISTS_ERROR_MESSAGE;
import static com.smartfactory.common.exception.QualityServiceExceptions.DEFECT_NOT_FOUND_ERROR_CODE;
import static com.smartfactory.common.exception.QualityServiceExceptions.DEFECT_NOT_FOUND_ERROR_MESSAGE;

import java.util.ArrayList;
import java.util.List;

import com.smartfactory.common.dto.quality.CreateDefectRequest;
import com.smartfactory.common.dto.quality.DefectResponse;
import com.smartfactory.common.exception.BusinessException;
import com.smartfactory.quality.control.repositories.DefectRepository;
import com.smartfactory.quality.control.utils.DefectMapper;
import com.smartfactory.quality.entity.Defect;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DefectService {

    @Inject
    DefectRepository defectRepository;

    @Transactional
    public DefectResponse createDefect(final CreateDefectRequest request) {

        if (defectRepository.findById(request.getCode()) != null) {
            throw new BusinessException(String.format(DEFECT_ALREADY_EXISTS_ERROR_MESSAGE, request.getCode()),
                    DEFECT_ALREADY_EXISTS_ERROR_CODE, 409);
        }

        final Defect defect = DefectMapper.toDefect(request);

        defectRepository.persist(defect);

        return DefectMapper.toResponse(defect);
    }

    public DefectResponse findByCode(final String code) {

        final Defect defect = defectRepository.findById(code);

        if (defect == null) {
            throw new BusinessException(String.format(DEFECT_NOT_FOUND_ERROR_MESSAGE, code),
                    DEFECT_NOT_FOUND_ERROR_CODE, 404);
        }

        return DefectMapper.toResponse(defect);
    }

    public List<DefectResponse> findAll() {

        final List<Defect> defects = defectRepository.listAll();
        final List<DefectResponse> responses = new ArrayList<>();

        for (final Defect defect : defects) {
            responses.add(DefectMapper.toResponse(defect));
        }

        return responses;
    }
}