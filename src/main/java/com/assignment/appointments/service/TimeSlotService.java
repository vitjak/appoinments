package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.AddTimeSlotRequest;
import com.assignment.appointments.model.TimeSlot;

import java.util.List;
import java.util.Optional;

public interface TimeSlotService {
    List<TimeSlot> getAvailableTimeSlotsByPractitioner(Long practitionerId);
    TimeSlot addTimeSlot(Long practitionerId, AddTimeSlotRequest addTimeSlotRequest);
    Optional<TimeSlot> getTimeSlotById(Long id);
    void markTimeSlotAsUnavailable(Long timeSlotId);
}