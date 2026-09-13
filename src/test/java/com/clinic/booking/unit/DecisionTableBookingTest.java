package com.clinic.booking.unit;

import com.clinic.booking.appointment.model.Appointment;
import com.clinic.booking.appointment.model.AppointmentState;
import com.clinic.booking.appointment.repository.AppointmentRepository;
import com.clinic.booking.appointment.service.AppointmentService;
import com.clinic.booking.appointment.service.AppointmentStateMachine;
import com.clinic.booking.appointment.service.CancellationFeeCalculator;
import com.clinic.booking.common.exception.BookingDomainException;
import com.clinic.booking.doctor.model.Doctor;
import com.clinic.booking.doctor.service.DoctorService;
import com.clinic.booking.patient.model.Patient;
import com.clinic.booking.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionTableBookingTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorService doctorService;

    @Mock
    private CancellationFeeCalculator feeCalculator;

    @Mock
    private AppointmentStateMachine stateMachine;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient validPatient;
    private Patient unpaidBillsPatient;
    private Doctor availableDoctor;
    private Doctor unavailableDoctor;

    @BeforeEach
    void setUp() {
        validPatient = new Patient(1L, "John Doe", false);
        unpaidBillsPatient = new Patient(2L, "Alice Smith", true);
        availableDoctor = new Doctor(10L, "Dr. Available", true);
        unavailableDoctor = new Doctor(20L, "Dr. Unavailable", false);
    }

    @Test
    @DisplayName("Rule 1: Registered = True, UnpaidBills = False, Doctor Available = True -> Success")
    void testRule1_Success() {
        when(patientService.findPatientById(1L)).thenReturn(Optional.of(validPatient));
        when(doctorService.findDoctorById(10L)).thenReturn(Optional.of(availableDoctor));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Appointment result = appointmentService.bookAppointment(1L, 10L, 48);

        assertThat(result).isNotNull();
        assertThat(result.getPatientId()).isEqualTo(1L);
        assertThat(result.getDoctorId()).isEqualTo(10L);
        assertThat(result.getState()).isEqualTo(AppointmentState.REQUESTED);
    }

    @Test
    @DisplayName("Rule 2: Registered = True, UnpaidBills = False, Doctor Available = False -> Fail (Doctor unavailable)")
    void testRule2_DoctorUnavailable() {
        when(patientService.findPatientById(1L)).thenReturn(Optional.of(validPatient));
        when(doctorService.findDoctorById(20L)).thenReturn(Optional.of(unavailableDoctor));

        assertThatThrownBy(() -> appointmentService.bookAppointment(1L, 20L, 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Doctor is currently unavailable");
    }

    @Test
    @DisplayName("Rule 3: Registered = True, UnpaidBills = True, Doctor Available = True -> Fail (Unpaid bills)")
    void testRule3_UnpaidBills() {
        when(patientService.findPatientById(2L)).thenReturn(Optional.of(unpaidBillsPatient));

        assertThatThrownBy(() -> appointmentService.bookAppointment(2L, 10L, 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Patient has unpaid bills");
    }

    @Test
    @DisplayName("Rule 4: Registered = True, UnpaidBills = True, Doctor Available = False -> Fail (Unpaid bills)")
    void testRule4_UnpaidBillsAndDoctorUnavailable() {
        when(patientService.findPatientById(2L)).thenReturn(Optional.of(unpaidBillsPatient));

        assertThatThrownBy(() -> appointmentService.bookAppointment(2L, 20L, 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Patient has unpaid bills");
    }

    @Test
    @DisplayName("Rule 5: Registered = False, UnpaidBills = False, Doctor Available = True -> Fail (Not registered)")
    void testRule5_UnregisteredPatient() {
        when(patientService.findPatientById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(99L, 10L, 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Patient is not registered");
    }

    @Test
    @DisplayName("Rule 6: Registered = False, UnpaidBills = False, Doctor Available = False -> Fail (Not registered)")
    void testRule6_UnregisteredPatientDoctorUnavailable() {
        when(patientService.findPatientById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(99L, 20L, 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Patient is not registered");
    }

    @Test
    @DisplayName("Rule 7: Registered = False (null ID), UnpaidBills = True, Doctor Available = True -> Fail (Not registered)")
    void testRule7_NullPatientId() {
        assertThatThrownBy(() -> appointmentService.bookAppointment(null, 10L, 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Patient is not registered");
    }

    @Test
    @DisplayName("Rule 8: Registered = False (null ID), Doctor Available = False -> Fail (Not registered)")
    void testRule8_NullPatientIdDoctorUnavailable() {
        assertThatThrownBy(() -> appointmentService.bookAppointment(null, 20L, 48))
                .isInstanceOf(BookingDomainException.class)
                .hasMessageContaining("Patient is not registered");
    }
}
