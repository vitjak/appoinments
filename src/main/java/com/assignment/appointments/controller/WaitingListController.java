package com.assignment.appointments.controller;

import com.assignment.appointments.dto.request.AddToWaitingListRequest;
import com.assignment.appointments.dto.request.AssignNextAvailableAppointmentRequest;
import com.assignment.appointments.service.WaitingListService;
import com.assignment.appointments.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Waiting List Management", description = "Endpoints for managing the waiting list and assigning appointments.")
@RestController
@RequestMapping("/api/waiting-list")
class WaitingListController {

    private final WaitingListService waitingListService;

    public WaitingListController(WaitingListService waitingListService) {
        this.waitingListService = waitingListService;
    }

    @Operation(summary = "Get waiting list entries by practitioner ID")
    @GetMapping
    public ResponseEntity<?> getWaitingListByPractitioner(@RequestParam Long practitionerId) {
        return ResponseBuilder
                .create()
                .status(HttpStatus.OK)
                .entity(waitingListService.getWaitingListByPractitioner(practitionerId))
                .build();
    }

    @Operation(summary = "Add patient to waiting list")
    @PostMapping("/add")
    public ResponseEntity<Object> addToWaitingList(@RequestBody AddToWaitingListRequest request) {
        try {
            waitingListService.addToWaitingList(request.getPatientId(), request.getPractitionerId());
            return ResponseEntity.ok().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @Operation(summary = "Assign next available appointment from waiting list")
    @PostMapping("/assign")
    public ResponseEntity<?> assignNextAvailableAppointment(@RequestBody AssignNextAvailableAppointmentRequest request) {
        try {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.OK)
                    .entity(waitingListService.assignNextAvailableAppointment(request.getPractitionerId(), request.getTimeSlotId(), request.getMedicalServiceId()))
                    .build();
        }
        catch (EntityNotFoundException e) {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
        catch (IllegalStateException e) {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }
}