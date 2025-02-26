package com.assignment.appointments.controller;

import com.assignment.appointments.dto.request.AddTimeSlotRequest;
import com.assignment.appointments.service.TimeSlotService;
import com.assignment.appointments.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Time Slot Management", description = "Endpoints for managing available time slots.")
@RestController
@RequestMapping("/api/timeslots")
class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @Operation(summary = "Add a new time slot for a practitioner")
    @PostMapping("/{practitionerId}/add")
    public ResponseEntity<?> addTimeSlot(
            @PathVariable Long practitionerId,
            @RequestBody AddTimeSlotRequest request) {

        return ResponseBuilder
                .create()
                .status(HttpStatus.OK)
                .entity(timeSlotService.addTimeSlot(practitionerId, request))
                .build();
    }

    @Operation(summary = "Get available time slots for a practitioner")
    @GetMapping("/practitioner/{practitionerId}")
    public ResponseEntity<?> getAvailableSlots(@PathVariable Long practitionerId) {
        return ResponseBuilder
                .create()
                .status(HttpStatus.OK)
                .entity(timeSlotService.getAvailableTimeSlotsByPractitioner(practitionerId))
                .build();
    }
}