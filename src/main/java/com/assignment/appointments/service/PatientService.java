package com.assignment.appointments.service;

import com.assignment.appointments.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {
    List<Patient> getAllPatients();
    Optional<Patient> getPatientById(Long id);
    Optional<Patient> getPatientByEmail(String email);
    Patient registerPatient(Patient patient);
}