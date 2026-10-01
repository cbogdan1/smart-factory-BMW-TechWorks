package com.smartfactory.quality.entity.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.quality.entity.Defect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class InspectionDecisionEngineTest {
    private final InspectionDecisionEngine engine = new InspectionDecisionEngine();

    @ParameterizedTest
    @CsvSource({ "100, PASS", " 85, PASS", " 80, PASS", " 79, REWORK", " 72, REWORK", " 50, REWORK", " 49, FAIL",
            " 45, FAIL", "  0, FAIL" })
    public void decide(final int score, final Decision expectedDecision) {
        assertEquals(expectedDecision, engine.decide(score));
    }

    @Test
    public void earliestStageWithOneDefect() {
        final Defect d = new Defect();
        d.setCode("PAINT_SCRATCH");
        d.setAffectedStage(ProductionStage.PAINT);

        assertEquals(ProductionStage.PAINT, engine.earliestProductionStage(List.of(d)));
    }

    @Test
    public void earliestStageReturnsTheLowestStage() {
        final Defect d1 = new Defect();
        d1.setCode("BATTERY_FAILURE");
        d1.setAffectedStage(ProductionStage.POWERTRAIN);

        final Defect d2 = new Defect();
        d2.setCode("MISSING_SEAT");
        d2.setAffectedStage(ProductionStage.INTERIOR);

        final Defect d3 = new Defect();
        d3.setCode("PAINT_SCRATCH");
        d3.setAffectedStage(ProductionStage.PAINT);

        assertEquals(ProductionStage.PAINT, engine.earliestProductionStage(List.of(d1, d2, d3)));
    }

    @Test
    public void earliestStageDoesNotDependOnTheOrderOfTheDefects() {
        final Defect d1 = new Defect();
        d1.setCode("DOOR_ALIGNMENT");
        d1.setAffectedStage(ProductionStage.INTERIOR);

        final Defect d2 = new Defect();
        d2.setCode("BATTERY_FAILURE");
        d2.setAffectedStage(ProductionStage.POWERTRAIN);

        assertEquals(ProductionStage.INTERIOR, engine.earliestProductionStage(List.of(d1, d2)));
        assertEquals(ProductionStage.INTERIOR, engine.earliestProductionStage(List.of(d2, d1)));
    }

    @Test
    public void earliestStageSkipsDefectsThatAreNotMapped() {
        final Defect unknown = new Defect();
        unknown.setCode("UNKNOWN_CODE");
        // affectedStage left null

        final Defect battery = new Defect();
        battery.setCode("BATTERY_FAILURE");
        battery.setAffectedStage(ProductionStage.POWERTRAIN);

        assertEquals(ProductionStage.POWERTRAIN, engine.earliestProductionStage(List.of(unknown, battery)));
    }

    @Test
    public void earliestStageIsNullWhenNoDefectIsMapped() {
        final Defect unknown = new Defect();
        unknown.setCode("UNKNOWN_CODE");
        // affectedStage left null

        assertNull(engine.earliestProductionStage(List.of(unknown)));
    }

    @Test
    public void earliestStageIsNullWithoutDefects() {
        assertNull(engine.earliestProductionStage(new ArrayList<>()));
    }

    @Test
    public void determineReasonPrefersDescription() {
        final Defect d = new Defect();
        d.setCode("PAINT_SCRATCH");
        d.setDescription("Paint scratch detected");
        d.setAffectedStage(ProductionStage.PAINT);

        final String reason = engine.determineReason(List.of(d), ProductionStage.PAINT);
        assertEquals("Paint scratch detected", reason);
    }

    @Test
    public void determineReasonFallsBackToCodeWhenNoDescription() {
        final Defect d = new Defect();
        d.setCode("PAINT_SCRATCH");
        d.setAffectedStage(ProductionStage.PAINT);

        final String reason = engine.determineReason(List.of(d), ProductionStage.PAINT);
        assertEquals("PAINT_SCRATCH", reason);
    }

    @Test
    public void determineReasonReturnsNullWhenNoDefectMatchesStage() {
        final Defect d = new Defect();
        d.setCode("MISSING_SEAT");
        d.setAffectedStage(ProductionStage.INTERIOR);

        final String reason = engine.determineReason(List.of(d), ProductionStage.PAINT);
        assertNull(reason);
    }
}