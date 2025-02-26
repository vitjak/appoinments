package com.assignment.appointments.service;

import com.assignment.appointments.model.MedicalService;
import com.assignment.appointments.model.Patient;
import com.assignment.appointments.model.Practitioner;
import com.assignment.appointments.repository.MedicalServiceRepository;
import com.assignment.appointments.repository.PractitionerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PractitionerServiceImplTest {

    @Mock
    private PractitionerRepository practitionerRepository;

    @Mock
    private MedicalServiceRepository medicalServiceRepository;

    @InjectMocks
    private PractitionerServiceImpl practitionerService;

    private Practitioner practitioner1;
    private Practitioner practitioner2;
    private MedicalService medicalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        practitioner1 = new Practitioner();
        practitioner1.setId(1L);
        practitioner1.setFirstName("Jože");
        practitioner1.setLastName("Novak");
        practitioner1.setSpecialization("Kardiologija");
        practitioner1.setMedicalServices(new HashSet<>());

        practitioner1 = new Practitioner();
        practitioner1.setId(1L);
        practitioner1.setFirstName("Marija");
        practitioner1.setLastName("Novakovič");
        practitioner1.setSpecialization("Radiologija");
        practitioner1.setMedicalServices(new HashSet<>());

        medicalService = new MedicalService();
        medicalService.setId(2L);
    }

    @Test
    void testGetAllPractitioners() {
        List<Practitioner> practitioners = Arrays.asList(practitioner1, practitioner2);

        when(practitionerRepository.findAll()).thenReturn(practitioners);

        List<Practitioner> result = practitionerService.getAllPractitioners();
        assertEquals(2, result.size());
        verify(practitionerRepository, times(1)).findAll();
    }

    @Test
    void testGetPractitionerByIdFound() {
        when(practitionerRepository.findById(1L)).thenReturn(Optional.of(practitioner1));

        Optional<Practitioner> result = practitionerService.getPractitionerById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetPractitionerByIdNotFound() {
        when(practitionerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Practitioner> result = practitionerService.getPractitionerById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void testAssignServiceToPractitionerSuccess() {
        when(practitionerRepository.findById(1L)).thenReturn(Optional.of(practitioner1));
        when(medicalServiceRepository.findById(2L)).thenReturn(Optional.of(medicalService));
        when(practitionerRepository.save(practitioner1)).thenReturn(practitioner1);

        Practitioner result = practitionerService.assignServiceToPractitioner(1L, 2L);
        assertNotNull(result);
        assertEquals(1, result.getMedicalServices().size());
        assertTrue(result.getMedicalServices().contains(medicalService));

        verify(practitionerRepository, times(1)).save(practitioner1);
    }

    @Test
    void testAssignServiceToPractitionerPractitionerNotFound() {
        when(practitionerRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> practitionerService.assignServiceToPractitioner(1L, 2L));

        assertEquals("Practitioner not found with ID: 1", exception.getMessage());
    }

    @Test
    void testAssignServiceToPractitionerServiceNotFound() {
        when(practitionerRepository.findById(1L)).thenReturn(Optional.of(practitioner1));
        when(medicalServiceRepository.findById(2L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> practitionerService.assignServiceToPractitioner(1L, 2L));

        assertEquals("Service not found with ID: 2", exception.getMessage());
    }
}
