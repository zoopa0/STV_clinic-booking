package com.clinic.booking.web.controller;

import com.clinic.booking.appointment.model.Appointment;
import com.clinic.booking.appointment.service.AppointmentService;
import com.clinic.booking.appointment.service.CancellationFeeCalculator;
import com.clinic.booking.common.exception.BookingDomainException;
import com.clinic.booking.common.exception.IllegalStateTransitionException;
import com.clinic.booking.doctor.model.Doctor;
import com.clinic.booking.doctor.service.DoctorService;
import com.clinic.booking.patient.model.Patient;
import com.clinic.booking.patient.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/appointments")
public class AppointmentWebController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final CancellationFeeCalculator feeCalculator;

    public AppointmentWebController(AppointmentService appointmentService,
                                    DoctorService doctorService,
                                    PatientService patientService,
                                    CancellationFeeCalculator feeCalculator) {
        this.appointmentService = appointmentService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.feeCalculator = feeCalculator;
    }

    @GetMapping
    public String listAppointments(HttpSession session, Model model) {
        Patient currentPatient = (Patient) session.getAttribute("currentPatient");
        if (currentPatient == null) {
            return "redirect:/login";
        }

        List<Appointment> appointments = appointmentService.findAllAppointments();
        List<Doctor> doctors = doctorService.findAllDoctors();
        List<Patient> patients = patientService.findAllPatients();

        Map<Long, String> doctorNames = new HashMap<>();
        for (Doctor d : doctors) {
            doctorNames.put(d.getId(), d.getName());
        }

        Map<Long, String> patientNames = new HashMap<>();
        for (Patient p : patients) {
            patientNames.put(p.getId(), p.getName());
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("doctorNames", doctorNames);
        model.addAttribute("patientNames", patientNames);
        model.addAttribute("currentPatient", currentPatient);
        return "appointments";
    }

    @PostMapping("/{id}/confirm")
    public String confirmAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.confirmAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment #" + id + " confirmed successfully.");
        } catch (IllegalStateTransitionException | BookingDomainException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/attend")
    public String attendAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.attendAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment #" + id + " marked as ATTENDED.");
        } catch (IllegalStateTransitionException | BookingDomainException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Appointment app = appointmentService.cancelAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", 
                    String.format("Appointment #%d cancelled. Cancellation Fee applied: $%.2f", id, app.getCancellationFee()));
        } catch (IllegalStateTransitionException | BookingDomainException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/appointments";
    }

    @GetMapping("/preview-fee")
    @ResponseBody
    public Map<String, Object> previewCancellationFee(@RequestParam("hours") int hours) {
        Map<String, Object> response = new HashMap<>();
        try {
            double fee = feeCalculator.calculateFee(hours);
            response.put("fee", fee);
            response.put("success", true);
        } catch (IllegalArgumentException ex) {
            response.put("error", ex.getMessage());
            response.put("success", false);
        }
        return response;
    }
}
