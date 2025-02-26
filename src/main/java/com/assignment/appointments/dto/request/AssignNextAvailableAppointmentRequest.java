package com.assignment.appointments.dto.request;

import lombok.Data;

@Data
public class AssignNextAvailableAppointmentRequest {
    private Long practitionerId;
    private Long timeSlotId;
    private Long medicalServiceId;
}
