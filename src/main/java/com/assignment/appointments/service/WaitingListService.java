package com.assignment.appointments.service;

import com.assignment.appointments.model.WaitingList;

import java.util.List;
import java.util.Optional;

public interface WaitingListService {
    void addToWaitingList(Long patientId, Long practitionerId);
    Optional<WaitingList> assignNextAvailableAppointment(Long practitionerId, Long timeSlotId, Long serviceId);
    List<WaitingList> getWaitingListByPractitioner(Long practitionerId);
}