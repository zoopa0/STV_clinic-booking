package com.clinic.booking.appointment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;

    private Long doctorId;

    @Enumerated(EnumType.STRING)
    private AppointmentState state;

    private int hoursUntilAppointment;

    private double cancellationFee;

    public Appointment() {
    }

    public Appointment(Long patientId, Long doctorId, int hoursUntilAppointment) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.hoursUntilAppointment = hoursUntilAppointment;
        this.state = AppointmentState.REQUESTED;
        this.cancellationFee = 0.0;
    }

    public Appointment(Long id, Long patientId, Long doctorId, AppointmentState state, int hoursUntilAppointment, double cancellationFee) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.state = state;
        this.hoursUntilAppointment = hoursUntilAppointment;
        this.cancellationFee = cancellationFee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AppointmentState getState() {
        return state;
    }

    public void setState(AppointmentState state) {
        this.state = state;
    }

    public int getHoursUntilAppointment() {
        return hoursUntilAppointment;
    }

    public void setHoursUntilAppointment(int hoursUntilAppointment) {
        this.hoursUntilAppointment = hoursUntilAppointment;
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }
}
