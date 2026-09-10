package com.clinic.booking.web.dto;

public class BookingRequestDto {

    private Long patientId;
    private Long doctorId;
    private Integer hoursUntilAppointment;

    public BookingRequestDto() {
    }

    public BookingRequestDto(Long patientId, Long doctorId, Integer hoursUntilAppointment) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.hoursUntilAppointment = hoursUntilAppointment;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getHoursUntilAppointment() {
        return hoursUntilAppointment;
    }

    public void setHoursUntilAppointment(Integer hoursUntilAppointment) {
        this.hoursUntilAppointment = hoursUntilAppointment;
    }
}
