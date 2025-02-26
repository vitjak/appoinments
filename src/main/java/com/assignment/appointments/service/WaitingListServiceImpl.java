package com.assignment.appointments.service;

import com.assignment.appointments.model.*;
import com.assignment.appointments.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
class WaitingListServiceImpl implements WaitingListService {

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
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID: " + patientId));

        Practitioner practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new EntityNotFoundException("Practitioner not found with ID: " + practitionerId));

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
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found with ID: " + timeSlotId));

        if (!timeSlot.getIsAvailable()) {
            throw new IllegalStateException("Selected time slot is not available.");
        }

        MedicalService medicalService = medicalServiceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with ID: " + serviceId));

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
