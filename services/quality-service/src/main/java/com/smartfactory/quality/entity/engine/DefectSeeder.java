package com.smartfactory.quality.entity.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.smartfactory.common.enums.Decision;
import com.smartfactory.quality.entity.Defect;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class DefectSeeder {

    static final int[][] ODDS = { { 40, 70, 90 },     // first arrival:   40% clean, 30% pass, 20% rework, 10% fail
            { 60, 85, 97 },     // after 1 rework:  60% clean, 25% pass, 12% rework,  3% fail
            { 75, 95, 100 },    // after 2 reworks: 75% clean, 20% pass,  5% rework,  0% fail
            { 100, 100, 100 }   // after 3 reworks: always clean
    };

    static final int MAX_DEFECTS_PER_INSPECTION = 4;

    private static final int MAX_SCORE = 100;

    private static final String DEFAULT_COMMENT = "No detailed observation recorded";

    private static final Map<String, String> COMMENTS =
            Map.of("PAINT_SCRATCH", "Scratch on the left rear door, approx. 4 cm, through clear coat", "DOOR_ALIGNMENT",
                    "Driver door gap uneven, 3 mm wider at the top than at the sill", "MISSING_SEAT",
                    "Rear bench not installed, mounting points empty", "BATTERY_FAILURE",
                    "Cell 7 below minimum voltage on end-of-line test");

    @Inject
    InspectionDecisionEngine inspectionDecisionEngine;

    @Inject
    DemoOutcomeControl demoOutcomeControl;

    // rolls 0–99 against the odds row for that rework count and returns a matching defect combination; a low roll returns an empty list (clean vehicle).
    public List<String> seedFor(final long reworkCount, final Collection<Defect> catalog) {
        // A demo operator can pin the outcome so happy-path / rework / failure can be scripted.
        if (demoOutcomeControl.isForced()) {
            final Decision forced = demoOutcomeControl.forcedDecision();

            if (forced == null) {
                return List.of();
            }

            return pickOne(groupByDecision(catalog), forced);
        }

        final int[] odds = ODDS[(int) Math.min(Math.max(reworkCount, 0), ODDS.length - 1L)];
        final int roll = roll(MAX_SCORE);

        if (roll < odds[0]) {
            return List.of();
        }

        final Map<Decision, List<List<String>>> byDecision = groupByDecision(catalog);

        if (roll < odds[1]) {
            return pickOne(byDecision, Decision.PASS);
        }
        if (roll < odds[2]) {
            return pickOne(byDecision, Decision.REWORK);
        }

        return pickOne(byDecision, Decision.FAIL);
    }

    public String commentFor(final String defectCode) {
        return COMMENTS.getOrDefault(defectCode, DEFAULT_COMMENT);
    }

    private Map<Decision, List<List<String>>> groupByDecision(final Collection<Defect> catalog) {
        final Map<Decision, List<List<String>>> byDecision = new EnumMap<>(Decision.class);
        for (final Decision decision : Decision.values()) {
            byDecision.put(decision, new ArrayList<>());
        }

        // build every combination of up to 4 catalog defects and file each under the decision its penalty points produce.
        collectCombinations(scorable(catalog), 0, new ArrayList<>(), byDecision);

        return byDecision;
    }

    // drops inactive defects
    private List<Defect> scorable(final Collection<Defect> catalog) {
        final List<Defect> scorable = new ArrayList<>();

        if (catalog == null) {
            return scorable;
        }

        for (final Defect defect : catalog) {
            if (defect != null && Boolean.TRUE.equals(defect.getActive()) && defect.getPenaltyPoints() != null) {
                scorable.add(defect);
            }
        }

        return scorable;
    }

    private void collectCombinations(final List<Defect> catalog, final int from, final List<Defect> current,
            final Map<Decision, List<List<String>>> byDecision) {

        if (!current.isEmpty()) {
            byDecision.get(inspectionDecisionEngine.decide(scoreOf(current))).add(codesOf(current));
        }

        if (current.size() == MAX_DEFECTS_PER_INSPECTION) {
            return;
        }

        for (int i = from; i < catalog.size(); i++) {
            current.add(catalog.get(i));
            collectCombinations(catalog, i + 1, current, byDecision);
            current.remove(current.size() - 1);
        }
    }

    private int scoreOf(final List<Defect> defects) {
        int penaltyPoints = 0;
        for (final Defect defect : defects) {
            penaltyPoints += defect.getPenaltyPoints();
        }

        return Math.max(0, MAX_SCORE - penaltyPoints);
    }

    private List<String> codesOf(final List<Defect> defects) {
        final List<String> codes = new ArrayList<>();
        for (final Defect defect : defects) {
            codes.add(defect.getCode());
        }

        return codes;
    }

    // picks from a group at random
    private List<String> pickOne(final Map<Decision, List<List<String>>> byDecision, final Decision decision) {
        final List<List<String>> combinations = byDecision.get(decision);

        if (combinations.isEmpty()) {
            return List.of();
        }

        return combinations.get(roll(combinations.size()));
    }

    int roll(final int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}