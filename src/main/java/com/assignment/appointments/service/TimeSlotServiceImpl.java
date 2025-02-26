package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.AddTimeSlotRequest;
import com.assignment.appointments.model.Practitioner;
import com.assignment.appointments.model.TimeSlot;
import com.assignment.appointments.repository.PractitionerRepository;
import com.assignment.appointments.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final PractitionerRepository practitionerRepository;

    public TimeSlotServiceImpl(TimeSlotRepository timeSlotRepository, PractitionerRepository practitionerRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.practitionerRepository = practitionerRepository;
    }

    @Override
    public List<TimeSlot> getAvailableTimeSlotsByPractitioner(Long practitionerId) {
        return timeSlotRepository.findByPractitionerIdAndIsAvailableTrue(practitionerId);
    }

    @Override
    public TimeSlot addTimeSlot(Long practitionerId, AddTimeSlotRequest request) {
        Practitioner practitioner = practitionerRepository.findById(practitionerId)
                .orElseThrow(() -> new EntityNotFoundException("Practitioner not found with ID: " + practitionerId));

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setPractitioner(practitioner);
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setIsAvailable(true);

        return timeSlotRepository.save(timeSlot);
    }

    @Override
    public Optional<TimeSlot> getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id);
    }

    @Override
    public void markTimeSlotAsUnavailable(Long timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found with ID: " + timeSlotId));

        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);
    }
}
