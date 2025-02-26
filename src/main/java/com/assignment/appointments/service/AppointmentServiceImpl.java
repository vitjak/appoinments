package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.BookAppointmentRequest;
import com.assignment.appointments.model.*;
import com.assignment.appointments.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
class AppointmentServiceImpl implements AppointmentService {

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
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID: " + request.getPatientId()));

        MedicalService medicalService = medicalServiceRepository.findById(request.getMedicalServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found with ID: " + request.getMedicalServiceId()));

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found with ID: " + request.getTimeSlotId()));

        if (!timeSlot.getIsAvailable()) {
            throw new IllegalStateException("Time slot is already occupied.");
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
                .orElseThrow(() -> new EntityNotFoundException("Appointment with ID " + appointmentId + " not found."));

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
