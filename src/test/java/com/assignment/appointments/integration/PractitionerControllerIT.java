package com.assignment.appointments.integration;

import com.assignment.appointments.model.Practitioner;
import com.assignment.appointments.repository.PractitionerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class PractitionerControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        practitionerRepository.deleteAll();

        List<Practitioner> practitioners = List.of(
                new Practitioner(null, "Marjan", "Novak", "Kardiologija", null, null),
                new Practitioner(null, "Ana", "Škufca", "Ortopedija", null, null)
        );

        practitionerRepository.saveAll(practitioners);
    }

    @Test
    void getAllPractitioners_shouldReturnAllPractitioners() throws Exception {
        mockMvc.perform(get("/api/practitioners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Marjan")))
                .andExpect(jsonPath("$[1].firstName", is("Ana")));
    }

    @Test
    void getPractitionerById_shouldReturnPractitioner() throws Exception {
        Practitioner practitioner = practitionerRepository.findAll().get(0);

        mockMvc.perform(get("/api/practitioners/{id}", practitioner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(practitioner.getFirstName())));
    }

    @Test
    void getPractitionerById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/practitioners/{id}", 44L))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Practitioner with ID not found: 44")));
    }

    @Test
    void assignService_shouldAssignServiceToPractitioner() throws Exception {
        Practitioner practitioner = practitionerRepository.findAll().get(0);
        Long practitionerId = practitioner.getId();
        Long serviceId = 1L;

        mockMvc.perform(post("/api/practitioners/{practitionerId}/services/{serviceId}", practitionerId, serviceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(practitionerId.intValue())));
    }

    @Test
    void assignService_shouldReturnNotFoundForInvalidPractitioner() throws Exception {
        Long invalidPractitionerId = 66L;
        Long serviceId = 1L;

        mockMvc.perform(post("/api/practitioners/{practitionerId}/services/{serviceId}", invalidPractitionerId, serviceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Practitioner not found with ID: 66")));
    }

    @Test
    void assignService_shouldReturnNotFoundForInvalidService() throws Exception {
        Practitioner practitioner = practitionerRepository.findAll().get(0);
        Long practitionerId = practitioner.getId();
        Long invalidServiceId = 77L;

        mockMvc.perform(post("/api/practitioners/{practitionerId}/services/{serviceId}", practitionerId, invalidServiceId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Service not found with ID: 77")));
    }
}
