package com.clinic.booking.integration;

import com.clinic.booking.appointment.model.Appointment;
import com.clinic.booking.appointment.model.AppointmentState;
import com.clinic.booking.appointment.repository.AppointmentRepository;
import com.clinic.booking.appointment.service.AppointmentService;
import com.clinic.booking.common.exception.BookingDomainException;
import com.clinic.booking.doctor.model.Doctor;
import com.clinic.booking.doctor.repository.DoctorRepository;
import com.clinic.booking.patient.model.Patient;
import com.clinic.booking.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class AppointmentIntegrationTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Patient cleanPatient;
    private Patient debtorPatient;
    private Doctor availableDoctor;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        doctorRepository.deleteAll();

        cleanPatient = patientRepository.save(new Patient("Clean Patient", false));
        debtorPatient = patientRepository.save(new Patient("Debtor Patient", true));
        availableDoctor = doctorRepository.save(new Doctor("Dr. House", true));
    }

    @Test
    @DisplayName("Integration Test: Full DB Persisted Booking -> Confirm -> Attend Flow")
    void testFullBookingAndLifecyclePersistence() {
        // 1. Book appointment
        Appointment appointment = appointmentService.bookAppointment(
                cleanPatient.getId(), availableDoctor.getId(), 48);

        assertThat(appointment.getId()).isNotNull();
        assertThat(appointment.getState()).isEqualTo(AppointmentState.REQUESTED);

        // 2. Confirm in DB
        Appointment confirmedAppt = appointmentService.confirmAppointment(appointment.getId());
        assertThat(confirmedAppt.getState()).isEqualTo(AppointmentState.CONFIRMED);

        // 3. Mark attended in DB
        Appointment attendedAppt = appointmentService.attendAppointment(appointment.getId());
        assertThat(attendedAppt.getState()).isEqualTo(AppointmentState.ATTENDED);
    }

    @Test
    @DisplayName("Integration Test: Unpaid Bills Patient Booking Rejection in Persistence Layer")
    void testDebtorPatientBookingRejection() {
        assertThatThrownBy(() -> appointmentService.bookAppointment(
                debtorPatient.getId(), availableDoctor.getId(), 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Patient has unpaid bills");

        assertThat(appointmentRepository.count()).isZero();
    }

    @Test
    @DisplayName("Integration Test: Cancellation Fee Calculation & Persistence")
    void testCancellationFeePersistence() {
        Appointment appointment = appointmentService.bookAppointment(
                cleanPatient.getId(), availableDoctor.getId(), 12); // 12h notice -> $100 fee

        Appointment cancelledAppt = appointmentService.cancelAppointment(appointment.getId());

        assertThat(cancelledAppt.getState()).isEqualTo(AppointmentState.CANCELLED);
        assertThat(cancelledAppt.getCancellationFee()).isEqualTo(100.0);
    }
}
