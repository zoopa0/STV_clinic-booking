package com.clinic.booking.appointment.service;

import com.clinic.booking.appointment.model.Appointment;
import com.clinic.booking.appointment.model.AppointmentState;
import com.clinic.booking.appointment.repository.AppointmentRepository;
import com.clinic.booking.common.exception.BookingDomainException;
import com.clinic.booking.doctor.model.Doctor;
import com.clinic.booking.doctor.service.DoctorService;
import com.clinic.booking.patient.model.Patient;
import com.clinic.booking.patient.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final CancellationFeeCalculator feeCalculator;
    private final AppointmentStateMachine stateMachine;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientService patientService,
                              DoctorService doctorService,
                              CancellationFeeCalculator feeCalculator,
                              AppointmentStateMachine stateMachine) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.feeCalculator = feeCalculator;
        this.stateMachine = stateMachine;
    }

    /**
     * FR1: Appointment Booking Logic.
     * Evaluates patient registration, unpaid bills status, and doctor availability.
     */
    public Appointment bookAppointment(Long patientId, Long doctorId, int hoursUntilAppointment) {
        if (patientId == null) {
            throw new BookingDomainException("Patient is not registered");
        }

        Optional<Patient> patientOpt = patientService.findPatientById(patientId);
        if (patientOpt.isEmpty()) {
            throw new BookingDomainException("Patient is not registered");
        }

        Patient patient = patientOpt.get();
        if (patient.isHasUnpaidBills()) {
            throw new BookingDomainException("Patient has unpaid bills");
        }

        if (doctorId == null) {
            throw new BookingDomainException("Doctor is currently unavailable");
        }

        Optional<Doctor> doctorOpt = doctorService.findDoctorById(doctorId);
        if (doctorOpt.isEmpty() || !doctorOpt.get().isAvailable()) {
            throw new BookingDomainException("Doctor is currently unavailable");
        }

        if (hoursUntilAppointment < 0) {
            throw new BookingDomainException("Notice hours cannot be negative");
        }

        Appointment appointment = new Appointment(patientId, doctorId, hoursUntilAppointment);
        return appointmentRepository.save(appointment);
    }

    public Appointment confirmAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentOrThrow(appointmentId);
        stateMachine.validateTransition(appointment.getState(), AppointmentState.CONFIRMED);
        appointment.setState(AppointmentState.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    public Appointment attendAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentOrThrow(appointmentId);
        stateMachine.validateTransition(appointment.getState(), AppointmentState.ATTENDED);
        appointment.setState(AppointmentState.ATTENDED);
        return appointmentRepository.save(appointment);
    }

    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentOrThrow(appointmentId);
        stateMachine.validateTransition(appointment.getState(), AppointmentState.CANCELLED);
        
        double fee = feeCalculator.calculateFee(appointment.getHoursUntilAppointment());
        appointment.setCancellationFee(fee);
        appointment.setState(AppointmentState.CANCELLED);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> findAllAppointments() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> findAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    private Appointment getAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new BookingDomainException("Appointment not found with ID: " + id));
    }
}
