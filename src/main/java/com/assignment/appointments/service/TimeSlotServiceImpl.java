package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.AddTimeSlotRequest;
import com.assignment.appointments.model.Practitioner;
import com.assignment.appointments.model.TimeSlot;
import com.assignment.appointments.repository.PractitionerRepository;
import com.assignment.appointments.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
class TimeSlotServiceImpl implements TimeSlotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSlotServiceImpl.class);
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
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Practitioner not found with ID: " + practitionerId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error adding timeslot: " + e.getMessage(), e);
                    return e;
                });

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
                .orElseThrow(() -> {
                    EntityNotFoundException e = new EntityNotFoundException("Time slot not found with ID: " + timeSlotId);

                    LOGGER.atError()
                            .addKeyValue("rawMessage", e.getMessage())
                            .addKeyValue("exception", e.getClass().getSimpleName())
                            .log("Error marking timeslot as unavailable: " + e.getMessage(), e);
                    return e;
                });

        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);
    }
}
