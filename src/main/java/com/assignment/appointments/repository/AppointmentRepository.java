package com.assignment.appointments.repository;

import com.assignment.appointments.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.timeSlot.practitioner.id = :practitionerId")
    List<Appointment> findByPractitionerId(@Param("practitionerId") Long practitionerId);

}