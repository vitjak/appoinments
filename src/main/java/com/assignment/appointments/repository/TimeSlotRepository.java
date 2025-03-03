package com.assignment.appointments.repository;

import com.assignment.appointments.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByPractitionerIdAndIsAvailableTrue(Long practitionerId);
}