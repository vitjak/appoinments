package com.assignment.appointments.dto.request;

import lombok.Data;

@Data
public class BookAppointmentRequest {
    private Long patientId;
    private Long medicalServiceId;
    private Long timeSlotId;
}
