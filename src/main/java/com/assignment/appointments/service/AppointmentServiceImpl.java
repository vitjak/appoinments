package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.BookAppointmentRequest;
import com.assignment.appointments.model.*;
import com.assignment.appointments.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
class AppointmentServiceImpl implements AppointmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PractitionerRepository practitionerRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final TimeSlotRepository timeSlotRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientRepository patientRepository,
                                  PractitionerRepository practitionerRepository,
                                  MedicalServiceRepository medicalServiceRepository,
                                  TimeSlotRepository timeSlotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.practitionerRepository = practitionerRepository;
        this.medicalServiceRepository = medicalServiceRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public Appointment bookAppointment(BookAppointmentRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Patient not found with ID: " + request.getPatientId());

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error booking appointment: " + e.getMessage(), e);
                    return e;
                });

        MedicalService medicalService = medicalServiceRepository.findById(request.getMedicalServiceId())
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Service not found with ID: " + request.getMedicalServiceId());

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error booking appointment: " + e.getMessage(), e);
                    return e;
                });

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Time slot not found with ID: " + request.getTimeSlotId());

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error booking appointment: " + e.getMessage(), e);
                    return e;
                });

        if (!timeSlot.getIsAvailable()) {
            IllegalStateException e = new IllegalStateException("Time slot not found with ID: " + request.getTimeSlotId());

            LOGGER.atError()
                    .addKeyValue("rawMessage", e.getMessage())
                    .addKeyValue("exception", e.getClass().getSimpleName())
                    .log("Error error booking appointment: " + e.getMessage(), e);
            throw e;
        }

        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setTimeSlot(timeSlot);
        appointment.setMedicalService(medicalService);
        appointment.setBookedAt(LocalDateTime.now());
        appointment.setStatus(Appointment.AppointmentStatus.BOOKED);

        return appointmentRepository.save(appointment);
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Appointment with ID " + appointmentId + " not found.");

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error cancelling appointment: " + e.getMessage(), e);
                    return e;
                });

        appointment.setStatus(Appointment.AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);

        TimeSlot timeSlot = appointment.getTimeSlot();
        timeSlot.setIsAvailable(true);
        timeSlotRepository.save(timeSlot);
    }

    @Override
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public List<Appointment> getAppointmentsByPractitioner(Long practitionerId) {
        return appointmentRepository.findByPractitionerId(practitionerId);
    }
}
