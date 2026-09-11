package com.clinic.booking.common.data;

import com.clinic.booking.doctor.model.Doctor;
import com.clinic.booking.doctor.repository.DoctorRepository;
import com.clinic.booking.patient.model.Patient;
import com.clinic.booking.patient.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public DataInitializer(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() == 0) {
            patientRepository.save(new Patient("John Doe", false));
            patientRepository.save(new Patient("Alice Smith", true));
            patientRepository.save(new Patient("Bob Johnson", false));
        }

        if (doctorRepository.count() == 0) {
            doctorRepository.save(new Doctor("Dr. Sarah Jenkins (Cardiology)", true));
            doctorRepository.save(new Doctor("Dr. Michael Chen (Neurology)", true));
            doctorRepository.save(new Doctor("Dr. Emily Taylor (Orthopedics)", false));
            doctorRepository.save(new Doctor("Dr. David Wilson (Dermatology)", true));
        }
    }
}
