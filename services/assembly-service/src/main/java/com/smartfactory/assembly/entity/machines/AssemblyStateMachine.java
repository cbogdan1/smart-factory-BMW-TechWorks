package com.smartfactory.assembly.entity.machines;

import static com.smartfactory.common.enums.ProductionStage.ASSEMBLED;
import static com.smartfactory.common.enums.ProductionStage.BODY;
import static com.smartfactory.common.enums.ProductionStage.CREATED;
import static com.smartfactory.common.enums.ProductionStage.FINAL;
import static com.smartfactory.common.enums.ProductionStage.INTERIOR;
import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.enums.ProductionStage.POWERTRAIN;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_STAGE_TRANSITION_ERROR_CODE;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_STAGE_TRANSITION_ERROR_MESSAGE;

import java.util.Map;

import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * State machine that manages the production stages.
 */

@ApplicationScoped
public class AssemblyStateMachine {
    private static final Map<ProductionStage, ProductionStage> NEXT_STAGE =
            Map.of(CREATED, BODY, BODY, PAINT, PAINT, INTERIOR, INTERIOR, POWERTRAIN, POWERTRAIN, FINAL, FINAL,
                    ASSEMBLED);

    /**
     * Validates if a transition from a state to another is valid.
     *
     * @param from the current production stage of a vehicle.
     * @param to   the next production stage of the vehicle.
     */
    public void assertTransition(final ProductionStage from, final ProductionStage to) {
        if (NEXT_STAGE.get(from) == null || !NEXT_STAGE.get(from).equals(to)) {
            throw new BusinessException(String.format(INVALID_STAGE_TRANSITION_ERROR_MESSAGE, from, to),
                    INVALID_STAGE_TRANSITION_ERROR_CODE, 409);
        }
    }

    /**
     * Advances to the next production stage.
     *
     * @param currentStage the current stage of the vehicle.
     * @return the next production stage of the vehicle.
     */
    public ProductionStage getNextTransition(final ProductionStage currentStage) {
        if (NEXT_STAGE.get(currentStage) == null) {
            throw new BusinessException(String.format(INVALID_STAGE_TRANSITION_ERROR_MESSAGE, currentStage, "onward"),
                    INVALID_STAGE_TRANSITION_ERROR_CODE, 409);
        }
        return NEXT_STAGE.get(currentStage);
    }
}
