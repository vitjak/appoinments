package com.assignment.appointments.service;

import com.assignment.appointments.model.*;
import com.assignment.appointments.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
class WaitingListServiceImpl implements WaitingListService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WaitingListServiceImpl.class);
    private final WaitingListRepository waitingListRepository;
    private final PractitionerRepository practitionerRepository;
    private final PatientRepository patientRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalServiceRepository medicalServiceRepository;

    public WaitingListServiceImpl(WaitingListRepository waitingListRepository,
                                  PractitionerRepository practitionerRepository,
                                  PatientRepository patientRepository,
                                  TimeSlotRepository timeSlotRepository,
                                  AppointmentRepository appointmentRepository,
                                  MedicalServiceRepository medicalServiceRepository) {
        this.waitingListRepository = waitingListRepository;
        this.practitionerRepository = practitionerRepository;
        this.patientRepository = patientRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalServiceRepository = medicalServiceRepository;
    }

    @Override
    public void addToWaitingList(Long patientId, Long practitionerId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Patient not found with ID: " + patientId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error adding patient to wait list: " + e.getMessage(), e);
                    return e;
                });

        Practitioner practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Practitioner not found with ID: " + practitionerId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error adding patient to wait list: " + e.getMessage(), e);
                    return e;
                });

        WaitingList waitingListEntry = new WaitingList();
        waitingListEntry.setPatient(patient);
        waitingListEntry.setPractitioner(practitioner);
        waitingListEntry.setAddedAt(LocalDateTime.now());

        waitingListRepository.save(waitingListEntry);
    }

    @Override
    public Optional<WaitingList> assignNextAvailableAppointment(Long practitionerId, Long timeSlotId, Long serviceId) {
        List<WaitingList> waitingList = waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(practitionerId);

        if (waitingList.isEmpty()) {
            return Optional.empty();
        }

        WaitingList nextInLine = waitingList.get(0);
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Time slot not found with ID: " + timeSlotId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error assigning next available appointment: " + e.getMessage(), e);
                    return e;
                });

        if (!timeSlot.getIsAvailable()) {
            IllegalStateException e = new IllegalStateException("Selected time slot is not available.");

            LOGGER.atError()
                    .addKeyValue("rawMessage", e.getMessage())
                    .addKeyValue("exception", e.getClass().getSimpleName())
                    .log("Error assigning next available appointment: " + e.getMessage(), e);
            throw e;
        }

        MedicalService medicalService = medicalServiceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Service not found with ID: " + serviceId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error assigning next available appointment: " + e.getMessage(), e);
                    return e;
                });

        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);

        Appointment appointment = new Appointment();
        appointment.setPatient(nextInLine.getPatient());
        appointment.setTimeSlot(timeSlot);
        appointment.setMedicalService(medicalService);
        appointment.setBookedAt(LocalDateTime.now());
        appointment.setStatus(Appointment.AppointmentStatus.BOOKED);

        appointmentRepository.save(appointment);
        waitingListRepository.delete(nextInLine);

        return Optional.of(nextInLine);
    }

    @Override
    public List<WaitingList> getWaitingListByPractitioner(Long practitionerId) {
        return waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(practitionerId);
    }
}
