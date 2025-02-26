package com.assignment.appointments.controller;

import com.assignment.appointments.model.Patient;
import com.assignment.appointments.service.PatientService;
import com.assignment.appointments.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Patient Management", description = "Endpoints for patient registration and retrieval.")
@RestController
@RequestMapping("/api/patients")
class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Get all patients")
    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        return ResponseBuilder
                .create()
                .status(HttpStatus.OK)
                .entity(patientService.getAllPatients())
                .build();
    }

    @Operation(summary = "Get patient by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(patient -> ResponseBuilder
                        .create()
                        .status(HttpStatus.OK)
                        .entity(patient)
                        .build())
                .orElse(ResponseBuilder
                        .create()
                        .status(HttpStatus.NOT_FOUND)
                        .entity("Patient with ID not found: " + id)
                        .build());
    }

    @Operation(summary = "Register a new patient")
    @PostMapping
    public ResponseEntity<?> registerPatient(@RequestBody Patient patient) {
        try {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.CREATED)
                    .entity(patientService.registerPatient(patient))
                    .build();
        }
        catch (IllegalArgumentException e) {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }
}