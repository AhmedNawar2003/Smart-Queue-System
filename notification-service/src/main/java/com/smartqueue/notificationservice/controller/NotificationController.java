package com.smartqueue.notificationservice.controller;

import com.smartqueue.notificationservice.dto.NotificationResponse;
import com.smartqueue.notificationservice.service.JwtService;
import com.smartqueue.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications",
        description = "User notifications management")
@SecurityRequirement(name = "Bearer Auth")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtService          jwtService;

    @GetMapping("/me")
    @Operation(summary = "Get my notifications")
    public ResponseEntity<Page<NotificationResponse>>
    getMyNotifications(
            @RequestHeader("Authorization") String authHeader,
            @PageableDefault(size = 20) Pageable pageable) {

        UUID userId = extractUserId(authHeader);
        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        userId, pageable));
    }

    @GetMapping("/me/unread-count")
    @Operation(summary = "Get unread notifications count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = extractUserId(authHeader);
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(
                Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = extractUserId(authHeader);
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead(
            @RequestHeader("Authorization") String authHeader) {

        UUID userId = extractUserId(authHeader);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    private UUID extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtService.extractUserId(token);
    }
}