package com.smartfactory.assembly.entity;

import static com.smartfactory.common.enums.ProductionStage.ASSEMBLED;
import static com.smartfactory.common.enums.ProductionStage.BODY;
import static com.smartfactory.common.enums.ProductionStage.CREATED;
import static com.smartfactory.common.enums.ProductionStage.FINAL;
import static com.smartfactory.common.enums.ProductionStage.INTERIOR;
import static com.smartfactory.common.enums.ProductionStage.PAINT;
import static com.smartfactory.common.enums.ProductionStage.POWERTRAIN;
import static com.smartfactory.common.exception.AssemblyServiceExceptions.INVALID_STAGE_TRANSITION_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Map;

import com.smartfactory.assembly.entity.machines.AssemblyStateMachine;
import com.smartfactory.common.enums.ProductionStage;
import com.smartfactory.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class AssemblyStateMachineTest {

    private static final Map<ProductionStage, ProductionStage> VALID_TRANSITIONS =
            Map.of(CREATED, BODY, BODY, PAINT, PAINT, INTERIOR, INTERIOR, POWERTRAIN, POWERTRAIN, FINAL, FINAL,
                    ASSEMBLED);

    private AssemblyStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new AssemblyStateMachine();
    }

    @Test
    void shouldAllowEveryValidTransition() {
        VALID_TRANSITIONS.forEach((fromStage, toStage) -> {

            final Executable validTransitionAction = () -> stateMachine.assertTransition(fromStage, toStage);

            assertDoesNotThrow(validTransitionAction);
        });
    }

    @Test
    void shouldThrowBusinessExceptionForEveryInvalidTransition() {
        VALID_TRANSITIONS.keySet().forEach(fromStage -> {
            Arrays.stream(ProductionStage.values()).filter(toStage -> !toStage.equals(VALID_TRANSITIONS.get(fromStage)))
                    .forEach(toStage -> {
                        final Class<BusinessException> expectedException = BusinessException.class;

                        final Executable invalidTransitionAction =
                                () -> stateMachine.assertTransition(fromStage, toStage);

                        assertThrows(expectedException, invalidTransitionAction);
                    });
        });
    }

    @Test
    void shouldReturnNextTransitionForEveryNonTerminalStage() {
        VALID_TRANSITIONS.forEach((currentStage, expectedNextStage) -> {

            final ProductionStage actualNextStage = stateMachine.getNextTransition(currentStage);

            assertEquals(expectedNextStage, actualNextStage);
        });
    }

    @Test
    void shouldThrowBusinessExceptionWhenNextTransitionFromAssembled() {
        final ProductionStage terminalStage = ASSEMBLED;
        final String expectedMessage = String.format(INVALID_STAGE_TRANSITION_ERROR_MESSAGE, ASSEMBLED, "onward");

        final BusinessException exception =
                assertThrows(BusinessException.class, () -> stateMachine.getNextTransition(terminalStage));

        assertEquals(expectedMessage, exception.getMessage());
    }
}