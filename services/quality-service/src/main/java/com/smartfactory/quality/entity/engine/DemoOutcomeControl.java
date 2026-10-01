package com.smartfactory.quality.entity.engine;

import java.util.concurrent.atomic.AtomicReference;

import com.smartfactory.common.enums.Decision;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Lets a demo operator pin the outcome of the next automatic inspection.
 *
 * <p>{@link DefectSeeder} normally rolls a random defect combination, which makes it impossible to
 * script a demo. When an outcome is forced here, the seeder deterministically picks a defect
 * combination that produces that decision instead of rolling.
 *
 * <p>The forced outcome is <strong>sticky</strong>: it stays in effect until it is changed or
 * cleared, so a multi-round rework demo keeps producing the same decision until the operator
 * switches it.
 */
@ApplicationScoped
public class DemoOutcomeControl {

    /**
     * Possible forced outcomes. {@code RANDOM} restores the normal probabilistic behaviour.
     */
    public enum Outcome {
        RANDOM,
        CLEAN,
        PASS,
        REWORK,
        FAIL
    }

    private final AtomicReference<Outcome> forcedOutcome = new AtomicReference<>(Outcome.RANDOM);

    public Outcome getForcedOutcome() {
        return forcedOutcome.get();
    }

    public void setForcedOutcome(final Outcome outcome) {
        forcedOutcome.set(outcome == null ? Outcome.RANDOM : outcome);
    }

    public boolean isForced() {
        return forcedOutcome.get() != Outcome.RANDOM;
    }

    /**
     * Maps the forced outcome onto the decision whose defect combinations must be used.
     * Returns {@code null} for {@code CLEAN} (no defects at all) and for {@code RANDOM}.
     */
    public Decision forcedDecision() {
        return switch (forcedOutcome.get()) {
            case PASS -> Decision.PASS;
            case REWORK -> Decision.REWORK;
            case FAIL -> Decision.FAIL;
            default -> null;
        };
    }
}

