package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.BookAppointmentRequest;
import com.assignment.appointments.model.Appointment;

import java.util.List;

public interface AppointmentService {
    Appointment bookAppointment(BookAppointmentRequest request);
    void cancelAppointment(Long appointmentId);
    List<Appointment> getAppointmentsByPatient(Long patientId);
    List<Appointment> getAppointmentsByPractitioner(Long practitionerId);
}