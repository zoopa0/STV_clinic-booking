package com.clinic.booking.appointment.service;

import com.clinic.booking.appointment.model.AppointmentState;
import com.clinic.booking.common.exception.IllegalStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class AppointmentStateMachine {

    private static final Map<AppointmentState, Set<AppointmentState>> ALLOWED_TRANSITIONS = Map.of(
            AppointmentState.REQUESTED, EnumSet.of(AppointmentState.CONFIRMED, AppointmentState.CANCELLED),
            AppointmentState.CONFIRMED, EnumSet.of(AppointmentState.ATTENDED, AppointmentState.CANCELLED),
            AppointmentState.ATTENDED, EnumSet.noneOf(AppointmentState.class),
            AppointmentState.CANCELLED, EnumSet.noneOf(AppointmentState.class)
    );

    public boolean canTransition(AppointmentState currentState, AppointmentState targetState) {
        if (currentState == null || targetState == null) {
            return false;
        }
        Set<AppointmentState> allowed = ALLOWED_TRANSITIONS.get(currentState);
        return allowed != null && allowed.contains(targetState);
    }

    public void validateTransition(AppointmentState currentState, AppointmentState targetState) {
        if (!canTransition(currentState, targetState)) {
            throw new IllegalStateTransitionException(
                    String.format("Invalid state transition from %s to %s", currentState, targetState)
            );
        }
    }
}
