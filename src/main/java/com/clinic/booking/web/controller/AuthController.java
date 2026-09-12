package com.clinic.booking.web.controller;

import com.clinic.booking.patient.model.Patient;
import com.clinic.booking.patient.service.PatientService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    private final PatientService patientService;

    public AuthController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("currentPatient") != null) {
            return "redirect:/doctors";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        List<Patient> patients = patientService.findAllPatients();
        model.addAttribute("patients", patients);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("patientId") Long patientId, HttpSession session, Model model) {
        Optional<Patient> patientOpt = patientService.findPatientById(patientId);
        if (patientOpt.isPresent()) {
            session.setAttribute("currentPatient", patientOpt.get());
            return "redirect:/doctors";
        }
        model.addAttribute("error", "Invalid Patient ID selected");
        model.addAttribute("patients", patientService.findAllPatients());
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
