package com.assignment.appointments.service;

import com.assignment.appointments.model.Patient;
import com.assignment.appointments.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
class PatientServiceImpl implements PatientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Override
    public Patient registerPatient(Patient patient) {
        if (patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            IllegalArgumentException e = new IllegalArgumentException("Patient with email " + patient.getEmail() + " already exists.");

            LOGGER.atError()
                    .addKeyValue("rawMessage", e.getMessage())
                    .addKeyValue("exception", e.getClass().getSimpleName())
                    .log("Error registering patient: " + e.getMessage(), e);
            throw e;
        }
        return patientRepository.save(patient);
    }
}