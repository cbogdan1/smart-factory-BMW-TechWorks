package com.smartfactory.quality.entity.engine;

import java.util.Collection;
import java.util.Objects;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.quality.entity.Defect;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InspectionDecisionEngine {

    private static final int PASS_THRESHOLD = 80;
    private static final int REWORK_THRESHOLD = 50;

    public Decision decide(final int score) {
        if (score >= PASS_THRESHOLD) {
            return Decision.PASS;
        }
        if (score >= REWORK_THRESHOLD) {
            return Decision.REWORK;
        }
        return Decision.FAIL;

    }

    public ProductionStage earliestProductionStage(final Collection<Defect> defects) {
        ProductionStage earliest = null;

        if (defects == null || defects.isEmpty()) {
            return null;
        }

        for (final Defect defect : defects) {
            if (defect == null) {
                continue;
            }
            final ProductionStage stage = defect.getAffectedStage();
            if (stage != null && (earliest == null || stage.ordinal() < earliest.ordinal())) {
                earliest = stage;
            }
        }

        return earliest;
    }

    public String determineReason(final Collection<Defect> defects, final ProductionStage targetStage) {
        if (targetStage == null || defects == null || defects.isEmpty()) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        for (final Defect defect : defects) {
            if (defect == null) {
                continue;
            }
            final ProductionStage stage = defect.getAffectedStage();
            if (Objects.equals(stage, targetStage)) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                final String token = defect.getDescription() != null && !defect.getDescription().isBlank() ?
                        defect.getDescription() :
                        defect.getCode();
                sb.append(token);
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }
}
