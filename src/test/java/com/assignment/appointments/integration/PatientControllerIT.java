package com.assignment.appointments.integration;

import com.assignment.appointments.model.Patient;
import com.assignment.appointments.repository.PatientRepository;
import com.assignment.appointments.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();

        List<Patient> patients = List.of(
                new Patient(null,"Mia", "Novak", "mia.novak@gmail.com", null, null),
                new Patient(null,"Tara", "Plevnik", "tara.plevnik@gmail.com", null, null)
        );

        patients.forEach(patientService::registerPatient);
    }

    @Test
    void getAllPatients_shouldReturnPatients() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void getPatientById_shouldReturnPatient_whenPatientExists() throws Exception {
        Patient patient = new Patient();
        patient.setFirstName("Mia");
        patient.setLastName("Novak");
        patient.setEmail("mia.novak@gmail.com");
        Patient savedPatient = patientService.registerPatient(patient);

        mockMvc.perform(get("/api/patients/" + savedPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Mia")))
                .andExpect(jsonPath("$.email", is("mia.novak@gmail.com")));
    }

    @Test
    void getPatientById_shouldReturnNotFound_whenPatientDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/patients/55"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Patient with ID not found: 55")));
    }

    @Test
    void registerPatient_shouldCreatePatient_whenDataIsValid() throws Exception {
        String patientJson = "{" +
                "\"firstName\": \"Jaka\"," +
                "\"lastName\": \"Kralj\"," +
                "\"email\": \"jaka.kralj@gmail.com\"}";

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstName", is("Jaka")))
                .andExpect(jsonPath("$.email", is("jaka.kralj@gmail.com")));
    }

    @Test
    void registerPatient_shouldReturnConflict_whenEmailExists() throws Exception {
        Patient patient = new Patient();
        patient.setFirstName("Marko");
        patient.setLastName("Petek");
        patient.setEmail("marko.petek@gmail.com");
        patientService.registerPatient(patient);

        String patientJson = "{" +
                "\"firstName\": \"Marko\"," +
                "\"lastName\": \"Petek\"," +
                "\"email\": \"marko.petek@gmail.com\"}";

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patientJson))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Patient with email marko.petek@gmail.com already exists.")));
    }
}
