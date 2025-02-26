package com.assignment.appointments.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddTimeSlotRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
