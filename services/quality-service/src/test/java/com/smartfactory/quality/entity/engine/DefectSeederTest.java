package com.smartfactory.quality.entity.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.quality.entity.Defect;
import org.junit.jupiter.api.Test;

public class DefectSeederTest {

    private static final int RUNS = 200;

    private static final InspectionDecisionEngine ENGINE = new InspectionDecisionEngine();

    private static final List<Defect> CATALOG = List.of(defect("PAINT_SCRATCH", 10, ProductionStage.PAINT),
            defect("DOOR_ALIGNMENT", 15, ProductionStage.INTERIOR),
            defect("MISSING_SEAT", 20, ProductionStage.INTERIOR),
            defect("BATTERY_FAILURE", 35, ProductionStage.POWERTRAIN));

    private static DefectSeeder seeder() {
        final DefectSeeder seeder = new DefectSeeder();
        seeder.inspectionDecisionEngine = new InspectionDecisionEngine();
        // A fresh control defaults to Outcome.RANDOM, so isForced() is false and the
        // seeder keeps using the probabilistic path these tests assert on.
        seeder.demoOutcomeControl = new DemoOutcomeControl();

        return seeder;
    }

    private static Defect defect(final String code, final int penaltyPoints, final ProductionStage stage) {
        final Defect defect = new Defect();
        defect.setCode(code);
        defect.setDescription(code);
        defect.setPenaltyPoints(penaltyPoints);
        defect.setAffectedStage(stage);
        defect.setActive(true);

        return defect;
    }

    @Test
    public void afterThreeReworksTheVehicleIsAlwaysClean() {
        for (int run = 0; run < RUNS; run++) {
            assertTrue(seeder().seedFor(3, CATALOG).isEmpty(), "run " + run);
            assertTrue(seeder().seedFor(9, CATALOG).isEmpty(), "run " + run);
        }
    }

    @Test
    public void unscorableDefectsAreNeverGenerated() {
        final Defect retired = defect("RETIRED", 10, ProductionStage.PAINT);
        retired.setActive(false);

        final Defect unscored = defect("UNSCORED", 10, ProductionStage.PAINT);
        unscored.setPenaltyPoints(null);

        final List<Defect> mixed = List.of(defect("PAINT_SCRATCH", 10, ProductionStage.PAINT), retired, unscored);

        for (int run = 0; run < RUNS; run++) {
            final List<String> generated = seeder().seedFor(0, mixed);
            assertTrue(generated.isEmpty() || generated.equals(List.of("PAINT_SCRATCH")), "generated " + generated);
        }
    }

    @Test
    public void aDefectCarriesItsOwnCommentOrTheGenericOne() {
        final DefectSeeder seeder = seeder();

        assertEquals("Cell 7 below minimum voltage on end-of-line test", seeder.commentFor("BATTERY_FAILURE"));
        assertEquals("No detailed observation recorded", seeder.commentFor("LOOSE_MIRROR"));
    }
}