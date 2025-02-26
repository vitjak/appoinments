package com.assignment.appointments.service;

import com.assignment.appointments.model.Patient;
import com.assignment.appointments.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient1 = new Patient();
        patient1.setId(1L);
        patient1.setFirstName("Jože");
        patient1.setLastName("Novak");
        patient1.setEmail("joze.novak@example.com");

        patient2 = new Patient();
        patient2.setId(1L);
        patient2.setFirstName("Marija");
        patient2.setLastName("Novakovič");
        patient2.setEmail("marija.novakovic@example.com");
    }

    @Test
    void testGetAllPatients() {
        List<Patient> patients = Arrays.asList(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void testGetPatientById() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        Optional<Patient> result = patientService.getPatientById(1L);

        assertTrue(result.isPresent());
        assertEquals("Jože", result.get().getFirstName());
        assertEquals("Novak", result.get().getLastName());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPatientByEmail() {
        when(patientRepository.findByEmail("joze.novak@example.com")).thenReturn(Optional.of(patient1));

        Optional<Patient> result = patientService.getPatientByEmail("joze.novak@example.com");

        assertTrue(result.isPresent());
        assertEquals("joze.novak@example.com", result.get().getEmail());
        verify(patientRepository, times(1)).findByEmail("joze.novak@example.com");
    }

    @Test
    void testRegisterPatient_Success() {
        when(patientRepository.findByEmail("joze.novak@example.com")).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(patient1);

        Patient savedPatient = patientService.registerPatient(patient1);

        assertNotNull(savedPatient);
        assertEquals("joze.novak@example.com", savedPatient.getEmail());

        verify(patientRepository, times(1)).findByEmail("joze.novak@example.com");
        verify(patientRepository, times(1)).save(patient1);
    }

    @Test
    void testRegisterPatient_EmailAlreadyExists() {
        when(patientRepository.findByEmail("joze.novak@example.com")).thenReturn(Optional.of(patient1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientService.registerPatient(patient1));

        assertEquals("Patient with email joze.novak@example.com already exists.", exception.getMessage());
        verify(patientRepository, times(1)).findByEmail("joze.novak@example.com");
        verify(patientRepository, times(0)).save(any(Patient.class));
    }
}
