package com.assignment.appointments.service;

import com.assignment.appointments.dto.request.AddTimeSlotRequest;
import com.assignment.appointments.model.Practitioner;
import com.assignment.appointments.model.TimeSlot;
import com.assignment.appointments.repository.PractitionerRepository;
import com.assignment.appointments.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimeSlotServiceImplTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private PractitionerRepository practitionerRepository;

    @InjectMocks
    private TimeSlotServiceImpl timeSlotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAvailableTimeSlotsByPractitioner() {
        TimeSlot timeSlot1 = new TimeSlot();
        TimeSlot timeSlot2 = new TimeSlot();
        List<TimeSlot> timeSlots = Arrays.asList(timeSlot1, timeSlot2);

        when(timeSlotRepository.findByPractitionerIdAndIsAvailableTrue(1L)).thenReturn(timeSlots);

        List<TimeSlot> result = timeSlotService.getAvailableTimeSlotsByPractitioner(1L);
        assertEquals(2, result.size());
        verify(timeSlotRepository, times(1)).findByPractitionerIdAndIsAvailableTrue(1L);
    }

    @Test
    void testAddTimeSlotSuccess() {
        Practitioner practitioner = new Practitioner();
        practitioner.setId(1L);

        AddTimeSlotRequest request = new AddTimeSlotRequest();
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusMinutes(30));

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setPractitioner(practitioner);
        timeSlot.setStartTime(request.getStartTime());
        timeSlot.setEndTime(request.getEndTime());
        timeSlot.setIsAvailable(true);

        when(practitionerRepository.findById(1L)).thenReturn(Optional.of(practitioner));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(timeSlot);

        TimeSlot result = timeSlotService.addTimeSlot(1L, request);
        assertNotNull(result);
        assertEquals(request.getStartTime(), result.getStartTime());
        assertTrue(result.getIsAvailable());
        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
    }

    @Test
    void testAddTimeSlotPractitionerNotFound() {
        AddTimeSlotRequest request = new AddTimeSlotRequest();
        request.setStartTime(LocalDateTime.now());
        request.setEndTime(LocalDateTime.now().plusMinutes(30));

        when(practitionerRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> timeSlotService.addTimeSlot(1L, request));

        assertEquals("Practitioner not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetTimeSlotByIdFound() {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

        Optional<TimeSlot> result = timeSlotService.getTimeSlotById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetTimeSlotByIdNotFound() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TimeSlot> result = timeSlotService.getTimeSlotById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void testMarkTimeSlotAsUnavailableSuccess() {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setIsAvailable(true);

        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(timeSlot);

        timeSlotService.markTimeSlotAsUnavailable(1L);

        assertFalse(timeSlot.getIsAvailable());
        verify(timeSlotRepository, times(1)).save(timeSlot);
    }

    @Test
    void testMarkTimeSlotAsUnavailableNotFound() {
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> timeSlotService.markTimeSlotAsUnavailable(1L));

        assertEquals("Time slot not found with ID: 1", exception.getMessage());
    }
}
