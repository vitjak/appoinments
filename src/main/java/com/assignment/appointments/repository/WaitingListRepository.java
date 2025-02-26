package com.assignment.appointments.repository;

import com.assignment.appointments.model.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingList, Long> {
    List<WaitingList> findByPractitionerIdOrderByAddedAtAsc(Long practitionerId);
}