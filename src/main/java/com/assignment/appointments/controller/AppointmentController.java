package com.assignment.appointments.controller;

import com.assignment.appointments.dto.request.BookAppointmentRequest;
import com.assignment.appointments.service.AppointmentService;
import com.assignment.appointments.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Appointment Management", description = "Endpoints for booking and canceling appointments.")
@RestController
@RequestMapping("/api/appointments")
class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @Operation(summary = "Book an appointment")
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookAppointmentRequest request) {
        try {
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.OK)
                    .entity(appointmentService.bookAppointment(request))
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

    @Operation(summary = "Cancel an appointment")
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        try {
            appointmentService.cancelAppointment(id);
            return ResponseBuilder
                    .create()
                    .status(HttpStatus.OK)
                    .entity("Appointment canceled")
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
