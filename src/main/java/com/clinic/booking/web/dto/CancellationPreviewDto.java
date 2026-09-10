package com.clinic.booking.web.dto;

public class CancellationPreviewDto {

    private Long appointmentId;
    private int hoursUntilAppointment;
    private double calculatedFee;

    public CancellationPreviewDto() {
    }

    public CancellationPreviewDto(Long appointmentId, int hoursUntilAppointment, double calculatedFee) {
        this.appointmentId = appointmentId;
        this.hoursUntilAppointment = hoursUntilAppointment;
        this.calculatedFee = calculatedFee;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getHoursUntilAppointment() {
        return hoursUntilAppointment;
    }

    public void setHoursUntilAppointment(int hoursUntilAppointment) {
        this.hoursUntilAppointment = hoursUntilAppointment;
    }

    public double getCalculatedFee() {
        return calculatedFee;
    }

    public void setCalculatedFee(double calculatedFee) {
        this.calculatedFee = calculatedFee;
    }
}
