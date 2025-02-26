package com.assignment.appointments.controller;

import com.assignment.appointments.service.PractitionerService;
import com.assignment.appointments.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Practitioner Management", description = "Endpoints for managing practitioners and their services.")
@RestController
@RequestMapping("/api/practitioners")
public class PractitionerController {

    private final PractitionerService practitionerService;

    public PractitionerController(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }

    @Operation(summary = "Get all practitioners")
    @GetMapping
    public ResponseEntity<?> getAllPractitioners() {
        return ResponseBuilder
                .create()
                .status(HttpStatus.OK)
                .entity(practitionerService.getAllPractitioners())
                .build();
    }

    @Operation(summary = "Get practitioner by ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPractitionerById(@PathVariable Long id) {
        return practitionerService.getPractitionerById(id)
                .map(practitioner -> ResponseBuilder
                        .create()
                        .status(HttpStatus.OK)
                        .entity(practitioner)
                        .build())
                .orElse(ResponseBuilder
                        .create()
                        .status(HttpStatus.NOT_FOUND)
                        .entity("Practitioner with ID not found: " + id)
                        .build());
    }

    @Operation(summary = "Assign a service to practitioner")
    @PostMapping("/{practitionerId}/services/{serviceId}")
    public ResponseEntity<?> assignService(@PathVariable Long practitionerId, @PathVariable Long serviceId) {
        try {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.OK)
                    .entity(practitionerService.assignServiceToPractitioner(practitionerId, serviceId))
                    .build();
        }
        catch (EntityNotFoundException e) {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
}