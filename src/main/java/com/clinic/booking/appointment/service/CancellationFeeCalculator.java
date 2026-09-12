package com.clinic.booking.appointment.service;

import org.springframework.stereotype.Component;

@Component
public class CancellationFeeCalculator {

    /**
     * Calculates cancellation fee based on notice hours.
     * FR2 Rules:
     * - 48+ hours notice: $0 fee
     * - 24 to 47 hours notice: $50 fee
     * - 0 to 23 hours notice: $100 fee
     * - < 0 hours notice: IllegalArgumentException
     */
    public double calculateFee(int hoursUntilAppointment) {
        if (hoursUntilAppointment < 0) {
            throw new IllegalArgumentException("Notice period hours cannot be negative: " + hoursUntilAppointment);
        }
        if (hoursUntilAppointment >= 48) {
            return 0.0;
        } else if (hoursUntilAppointment >= 24) {
            return 50.0;
        } else {
            return 100.0;
        }
    }
}
