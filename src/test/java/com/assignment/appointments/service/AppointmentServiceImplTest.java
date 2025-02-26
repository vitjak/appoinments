package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.BookAppointmentRequest;
import com.assignment.appointments.model.*;
import com.assignment.appointments.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private MedicalServiceRepository medicalServiceRepository;
    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void bookAppointment_bookAppointmentSuccessfullly() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setPatientId(1L);
        request.setMedicalServiceId(2L);
        request.setTimeSlotId(3L);

        Patient patient = new Patient();
        MedicalService medicalService = new MedicalService();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setIsAvailable(true);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(medicalServiceRepository.findById(2L)).thenReturn(Optional.of(medicalService));
        when(timeSlotRepository.findById(3L)).thenReturn(Optional.of(timeSlot));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(new Appointment());

        Appointment appointment = appointmentService.bookAppointment(request);
        assertThat(appointment).isNotNull();
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void bookAppointment_throwExceptionIfPatientNotFound() {
        BookAppointmentRequest request = new BookAppointmentRequest();
        request.setPatientId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.bookAppointment(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }

    @Test
    void cancelAppointment_cancelAppointmentSuccessfully() {
        Appointment appointment = new Appointment();
        appointment.setStatus(Appointment.AppointmentStatus.BOOKED);
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setIsAvailable(false);
        appointment.setTimeSlot(timeSlot);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(1L);

        assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.CANCELED);
        assertThat(timeSlot.getIsAvailable()).isTrue();
        verify(appointmentRepository).save(appointment);
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void cancelAppointment_throwExceptionIfAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.cancelAppointment(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Appointment with ID 1 not found");
    }

    @Test
    void getAppointmentsByPatient_returnAppointmentsListSuccessfully() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(new Appointment()));

        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(1L);

        assertThat(appointments).isNotEmpty();
        verify(appointmentRepository).findByPatientId(1L);
    }

    @Test
    void getAppointmentsByPractitioner_returnAppointmentsListSuccessfully() {
        when(appointmentRepository.findByPractitionerId(1L)).thenReturn(List.of(new Appointment()));

        List<Appointment> appointments = appointmentService.getAppointmentsByPractitioner(1L);

        assertThat(appointments).isNotEmpty();
        verify(appointmentRepository).findByPractitionerId(1L);
    }
}
