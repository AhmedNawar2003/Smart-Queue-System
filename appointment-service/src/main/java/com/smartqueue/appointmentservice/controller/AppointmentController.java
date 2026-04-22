package com.smartqueue.appointmentservice.controller;

import com.smartqueue.appointmentservice.dto.AppointmentResponse;
import com.smartqueue.appointmentservice.dto.CreateAppointmentRequest;
import com.smartqueue.appointmentservice.dto.RescheduleRequest;
import com.smartqueue.appointmentservice.model.AppointmentLog;
import com.smartqueue.appointmentservice.service.AppointmentService;
import com.smartqueue.appointmentservice.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments",
        description = "Appointment booking and management")
@SecurityRequirement(name = "Bearer Auth")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtService         jwtService;

    // ── Book ──────────────────────────────────────────────────
    @PostMapping
    @Operation(summary = "Book a new appointment")
    public ResponseEntity<AppointmentResponse> book(
            @Valid @RequestBody CreateAppointmentRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token      = authHeader.substring(7);
        UUID   userId     = jwtService.extractUserId(token);
        String userEmail  = jwtService.extractEmail(token);
        String fullName   = jwtService.extractFullName(token);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.book(
                        request, userId, userEmail,
                        fullName != null ? fullName : userEmail));
    }

    // ── My Appointments ───────────────────────────────────────
    @GetMapping("/me")
    @Operation(summary = "Get my appointments")
    public ResponseEntity<Page<AppointmentResponse>>
    getMyAppointments(
            @RequestHeader("Authorization") String authHeader,
            @PageableDefault(size = 10) Pageable pageable) {

        String token  = authHeader.substring(7);
        UUID   userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(
                appointmentService.getMyAppointments(
                        userId, pageable));
    }

    // ── Get by ID ─────────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentResponse> getById(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        String token  = authHeader.substring(7);
        UUID   userId = jwtService.extractUserId(token);
        String role   = jwtService.extractRole(token);

        return ResponseEntity.ok(
                appointmentService.getById(id, userId, role));
    }

    // ── Reschedule ────────────────────────────────────────────
    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an appointment")
    public ResponseEntity<AppointmentResponse> reschedule(
            @PathVariable UUID id,
            @Valid @RequestBody RescheduleRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token  = authHeader.substring(7);
        UUID   userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(
                appointmentService.reschedule(id, userId, request));
    }

    // ── Cancel ────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an appointment")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        String token  = authHeader.substring(7);
        UUID   userId = jwtService.extractUserId(token);
        String role   = jwtService.extractRole(token);

        appointmentService.cancel(id, userId, role);
        return ResponseEntity.noContent().build();
    }

    // ── Center Appointments ───────────────────────────────────
    @GetMapping("/center/{centerId}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Operation(summary = "Get center appointments — Staff/Admin")
    public ResponseEntity<List<AppointmentResponse>>
    getCenterAppointments(
            @PathVariable UUID centerId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to) {

        return ResponseEntity.ok(
                appointmentService.getCenterAppointments(
                        centerId, from, to));
    }

    // ── Logs ──────────────────────────────────────────────────
    @GetMapping("/{id}/logs")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Operation(summary = "Get appointment logs — Staff/Admin")
    public ResponseEntity<List<AppointmentLog>> getLogs(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                appointmentService.getLogs(id));
    }
}