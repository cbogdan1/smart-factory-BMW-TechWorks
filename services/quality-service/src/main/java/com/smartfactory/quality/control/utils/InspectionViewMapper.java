package com.smartfactory.quality.control.utils;

import java.util.List;

import com.smartfactory.common.dto.quality.InspectionDefectView;
import com.smartfactory.common.dto.quality.InspectionView;
import com.smartfactory.quality.entity.Defect;
import com.smartfactory.quality.entity.Inspection;
import com.smartfactory.quality.entity.InspectionDefect;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InspectionViewMapper {

    public InspectionView toView(final Inspection inspection, final List<InspectionDefectView> defects) {

        final InspectionView view = new InspectionView();
        view.setInspectionId(inspection.getInspectionId());
        view.setVehicleId(inspection.getVehicleId());
        view.setInspectionDate(inspection.getInspectionDate());
        view.setStatus(inspection.getStatus());
        view.setScore(inspection.getScore());
        view.setDefects(defects);
        view.setDecision(inspection.getDecision());
        view.setTargetStage(inspection.getTargetStage());
        view.setInspector(inspection.getInspector());

        return view;
    }

    public InspectionDefectView toDefectView(final InspectionDefect inspectionDefect, final Defect defect) {

        final InspectionDefectView view = new InspectionDefectView();
        view.setDefectCode(inspectionDefect.getDefectCode());
        view.setComment(inspectionDefect.getComment());

        if (defect != null) {
            view.setDescription(defect.getDescription());
            view.setPenaltyPoints(defect.getPenaltyPoints());
        }

        return view;
    }
}
