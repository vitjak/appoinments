package com.assignment.appointments.dto.request;

import lombok.Data;

@Data
public class AddToWaitingListRequest {
    private Long patientId;
    private Long practitionerId;
}
