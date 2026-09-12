package com.clinic.booking.web.controller;

import com.clinic.booking.appointment.model.Appointment;
import com.clinic.booking.appointment.service.AppointmentService;
import com.clinic.booking.common.exception.BookingDomainException;
import com.clinic.booking.doctor.model.Doctor;
import com.clinic.booking.doctor.service.DoctorService;
import com.clinic.booking.patient.model.Patient;
import com.clinic.booking.patient.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorWebController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PatientService patientService;

    public DoctorWebController(DoctorService doctorService,
                               AppointmentService appointmentService,
                               PatientService patientService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @GetMapping
    public String listDoctors(HttpSession session, Model model) {
        Patient currentPatient = (Patient) session.getAttribute("currentPatient");
        if (currentPatient == null) {
            return "redirect:/login";
        }

        // Refresh current patient state from DB
        patientService.findPatientById(currentPatient.getId()).ifPresent(p -> session.setAttribute("currentPatient", p));
        currentPatient = (Patient) session.getAttribute("currentPatient");

        List<Doctor> doctors = doctorService.findAllDoctors();
        model.addAttribute("doctors", doctors);
        model.addAttribute("currentPatient", currentPatient);
        return "doctors";
    }

    @PostMapping("/book")
    public String bookAppointment(@RequestParam("doctorId") Long doctorId,
                                  @RequestParam(value = "patientId", required = false) Long patientId,
                                  @RequestParam(value = "hoursUntilAppointment", defaultValue = "48") int hoursUntilAppointment,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Patient currentPatient = (Patient) session.getAttribute("currentPatient");
        Long actualPatientId = (patientId != null) ? patientId : (currentPatient != null ? currentPatient.getId() : null);

        try {
            Appointment appointment = appointmentService.bookAppointment(actualPatientId, doctorId, hoursUntilAppointment);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Appointment successfully booked! Appointment ID: #" + appointment.getId());
            return "redirect:/appointments";
        } catch (BookingDomainException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/doctors";
        }
    }
}
