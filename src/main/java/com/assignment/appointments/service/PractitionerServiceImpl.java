package com.assignment.appointments.service;

import com.assignment.appointments.model.MedicalService;
import com.assignment.appointments.model.Practitioner;
import com.assignment.appointments.repository.MedicalServiceRepository;
import com.assignment.appointments.repository.PractitionerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
class PractitionerServiceImpl implements PractitionerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PractitionerServiceImpl.class);
    private final PractitionerRepository practitionerRepository;
    private final MedicalServiceRepository medicalServiceRepository;

    public PractitionerServiceImpl(PractitionerRepository practitionerRepository, MedicalServiceRepository medicalServiceRepository) {
        this.practitionerRepository = practitionerRepository;
        this.medicalServiceRepository = medicalServiceRepository;
    }

    @Override
    public List<Practitioner> getAllPractitioners() {
        return practitionerRepository.findAll();
    }

    @Override
    public Optional<Practitioner> getPractitionerById(Long id) {
        return practitionerRepository.findById(id);
    }

    @Override
    public Practitioner assignServiceToPractitioner(Long practitionerId, Long serviceId) {
        Practitioner practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Practitioner not found with ID: " + practitionerId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error assigning service to practitioner: " + e.getMessage(), e);
                    return e;
                });

        MedicalService medicalService = medicalServiceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Service not found with ID: " + serviceId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error assigning service to practitioner: " + e.getMessage(), e);
                    return e;
                });

        practitioner.getMedicalServices().add(medicalService);
        return practitionerRepository.save(practitioner);
    }
}