package com.assignment.appointments.service;

import com.assignment.appointments.model.*;
import com.assignment.appointments.repository.*;
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

class WaitingListServiceImplTest {

    @Mock
    private WaitingListRepository waitingListRepository;

    @Mock
    private PractitionerRepository practitionerRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MedicalServiceRepository medicalServiceRepository;

    @InjectMocks
    private WaitingListServiceImpl waitingListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToWaitingListSuccess() {
        Patient patient = new Patient();
        Practitioner practitioner = new Practitioner();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(practitionerRepository.findById(1L)).thenReturn(Optional.of(practitioner));

        waitingListService.addToWaitingList(1L, 1L);

        verify(waitingListRepository, times(1)).save(any(WaitingList.class));
    }

    @Test
    void testAddToWaitingListPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> waitingListService.addToWaitingList(1L, 1L));

        assertEquals("Patient not found with ID: 1", exception.getMessage());
    }

    @Test
    void testAddToWaitingListPractitionerNotFound() {
        Patient patient = new Patient();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(practitionerRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> waitingListService.addToWaitingList(1L, 1L));

        assertEquals("Practitioner not found with ID: 1", exception.getMessage());
    }

    @Test
    void testAssignNextAvailableAppointmentSuccess() {
        WaitingList waitingListEntry = new WaitingList();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setIsAvailable(true);
        MedicalService medicalService = new MedicalService();

        when(waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(1L))
                .thenReturn(Collections.singletonList(waitingListEntry));
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));

        Optional<WaitingList> result = waitingListService.assignNextAvailableAppointment(1L, 1L, 1L);

        assertTrue(result.isPresent());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
        verify(waitingListRepository, times(1)).delete(waitingListEntry);
    }

    @Test
    void testAssignNextAvailableAppointmentEmptyWaitingList() {
        when(waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(1L))
                .thenReturn(Collections.emptyList());

        Optional<WaitingList> result = waitingListService.assignNextAvailableAppointment(1L, 1L, 1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testAssignNextAvailableAppointmentTimeSlotUnavailable() {
        WaitingList waitingListEntry = new WaitingList();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setIsAvailable(false);

        when(waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(1L))
                .thenReturn(Collections.singletonList(waitingListEntry));
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> waitingListService.assignNextAvailableAppointment(1L, 1L, 1L));

        assertEquals("Selected time slot is not available.", exception.getMessage());
    }

    @Test
    void testAssignNextAvailableAppointmentTimeSlotNotFound() {
        WaitingList waitingListEntry = new WaitingList();

        when(waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(1L))
                .thenReturn(Collections.singletonList(waitingListEntry));
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> waitingListService.assignNextAvailableAppointment(1L, 1L, 1L));

        assertEquals("Time slot not found with ID: 1", exception.getMessage());
    }

    @Test
    void testAssignNextAvailableAppointmentServiceNotFound() {
        WaitingList waitingListEntry = new WaitingList();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setIsAvailable(true);

        when(waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(1L))
                .thenReturn(Collections.singletonList(waitingListEntry));
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(timeSlot));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> waitingListService.assignNextAvailableAppointment(1L, 1L, 1L));

        assertEquals("Service not found with ID: 1", exception.getMessage());
    }

    @Test
    void testGetWaitingListByPractitioner() {
        WaitingList waitingListEntry = new WaitingList();
        when(waitingListRepository.findByPractitionerIdOrderByAddedAtAsc(1L))
                .thenReturn(Collections.singletonList(waitingListEntry));

        List<WaitingList> result = waitingListService.getWaitingListByPractitioner(1L);

        assertEquals(1, result.size());
        assertEquals(waitingListEntry, result.get(0));
    }
}
