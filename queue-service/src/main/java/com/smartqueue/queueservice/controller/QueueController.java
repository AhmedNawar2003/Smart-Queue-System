package com.smartqueue.queueservice.controller;

import com.smartqueue.queueservice.dto.*;
import com.smartqueue.queueservice.model.QueueEntry;
import com.smartqueue.queueservice.service.JwtService;
import com.smartqueue.queueservice.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/queues")
@RequiredArgsConstructor
@Tag(name = "Queue", description = "Queue management and real-time tracking")
@SecurityRequirement(name = "Bearer Auth")
public class QueueController {

    private final QueueService queueService;
    private final JwtService   jwtService;

    // ── Staff/Admin endpoints ─────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Operation(summary = "Create a new queue for today — Staff/Admin")
    public ResponseEntity<QueueResponse> createQueue(
            @Valid @RequestBody CreateQueueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(queueService.createQueue(request));
    }

    @PostMapping("/{queueId}/next")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Operation(summary = "Call next ticket — Staff/Admin")
    public ResponseEntity<Void> callNext(
            @PathVariable UUID queueId) {
        queueService.callNext(queueId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/skip/{ticketNumber}")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Operation(summary = "Skip absent user — Staff/Admin")
    public ResponseEntity<Void> skipTicket(
            @PathVariable UUID queueId,
            @PathVariable int ticketNumber) {
        queueService.skipTicket(queueId, ticketNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{queueId}/close")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Operation(summary = "Close queue — Staff/Admin")
    public ResponseEntity<QueueResponse> closeQueue(
            @PathVariable UUID queueId) {
        return ResponseEntity.ok(queueService.closeQueue(queueId));
    }

    @GetMapping("/{queueId}/entries")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    @Operation(summary = "Get all entries in queue — Staff/Admin")
    public ResponseEntity<List<QueueEntry>> getEntries(
            @PathVariable UUID queueId) {
        return ResponseEntity.ok(queueService.getQueueEntries(queueId));
    }

    // ── User endpoints ────────────────────────────────────────

    @GetMapping("/{queueId}")
    @Operation(summary = "Get queue details")
    public ResponseEntity<QueueResponse> getQueue(
            @PathVariable UUID queueId) {
        return ResponseEntity.ok(queueService.getQueue(queueId));
    }

    @GetMapping("/center/{centerId}/today")
    @Operation(summary = "Get today's queue for a center")
    public ResponseEntity<QueueResponse> getTodayQueue(
            @PathVariable UUID centerId) {
        return ResponseEntity.ok(queueService.getTodayQueue(centerId));
    }

    @PostMapping("/{queueId}/join")
    @Operation(summary = "Join a queue — get your ticket")
    public ResponseEntity<JoinQueueResponse> joinQueue(
            @PathVariable UUID queueId,
            @RequestHeader("Authorization") String authHeader) {

        String token     = authHeader.substring(7);
        String email     = jwtService.extractEmail(token);
        String role      = jwtService.extractRole(token);
        // نستخدم email كـ userId مؤقتاً لو مفيش userId في الـ token
        UUID userId      = UUID.nameUUIDFromBytes(email.getBytes());

        return ResponseEntity.ok(
                queueService.joinQueue(queueId, userId, email, email));
    }

    @GetMapping("/{queueId}/position/{ticketNumber}")
    @Operation(summary = "Get your current position and ETA")
    public ResponseEntity<QueuePositionResponse> getPosition(
            @PathVariable UUID queueId,
            @PathVariable int ticketNumber) {
        return ResponseEntity.ok(
                queueService.getPosition(queueId, ticketNumber));
    }

    @DeleteMapping("/{queueId}/cancel")
    @Operation(summary = "Cancel your ticket")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable UUID queueId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        UUID userId  = UUID.nameUUIDFromBytes(email.getBytes());

        queueService.cancelTicket(queueId, userId);
        return ResponseEntity.noContent().build();
    }
}