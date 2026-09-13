package com.clinic.booking.unit;

import com.clinic.booking.appointment.model.AppointmentState;
import com.clinic.booking.appointment.service.AppointmentStateMachine;
import com.clinic.booking.common.exception.IllegalStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StateTransitionTest {

    private AppointmentStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new AppointmentStateMachine();
    }

    @ParameterizedTest(name = "Valid Transition: {0} -> {1}")
    @CsvSource({
            "REQUESTED, CONFIRMED",
            "REQUESTED, CANCELLED",
            "CONFIRMED, ATTENDED",
            "CONFIRMED, CANCELLED"
    })
    @DisplayName("FR3 Valid State Transitions")
    void testValidStateTransitions(AppointmentState from, AppointmentState to) {
        boolean canTransition = stateMachine.canTransition(from, to);
        assertThat(canTransition).isTrue();

        // Validating transition does not throw exception
        stateMachine.validateTransition(from, to);
    }

    @ParameterizedTest(name = "Illegal Transition: {0} -> {1}")
    @CsvSource({
            "REQUESTED, ATTENDED",
            "CANCELLED, CONFIRMED",
            "CANCELLED, ATTENDED",
            "CANCELLED, REQUESTED",
            "ATTENDED, CONFIRMED",
            "ATTENDED, CANCELLED",
            "ATTENDED, REQUESTED"
    })
    @DisplayName("FR3 Illegal State Transitions (Exception expected)")
    void testIllegalStateTransitions(AppointmentState from, AppointmentState to) {
        boolean canTransition = stateMachine.canTransition(from, to);
        assertThat(canTransition).isFalse();

        assertThatThrownBy(() -> stateMachine.validateTransition(from, to))
                .isInstanceOf(IllegalStateTransitionException.class)
                .hasMessageContaining(String.format("Invalid state transition from %s to %s", from, to));
    }
}
