package com.assignment.appointments.service;

import com.assignment.appointments.model.Practitioner;

import java.util.List;
import java.util.Optional;

public interface PractitionerService {
    List<Practitioner> getAllPractitioners();
    Optional<Practitioner> getPractitionerById(Long id);
    Practitioner assignServiceToPractitioner(Long practitionerId, Long serviceId);
}