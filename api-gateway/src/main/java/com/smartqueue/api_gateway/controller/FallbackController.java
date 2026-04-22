package com.smartqueue.api_gateway.controller;

import com.smartqueue.api_gateway.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<ErrorResponse> authFallback() {
        return buildFallback("Auth Service");
    }

    @GetMapping("/user")
    public ResponseEntity<ErrorResponse> userFallback() {
        return buildFallback("User Service");
    }

    @GetMapping("/queue")
    public ResponseEntity<ErrorResponse> queueFallback() {
        return buildFallback("Queue Service");
    }

    @GetMapping("/appointment")
    public ResponseEntity<ErrorResponse> appointmentFallback() {
        return buildFallback("Appointment Service");
    }

    @GetMapping("/notification")
    public ResponseEntity<ErrorResponse> notificationFallback() {
        return buildFallback("Notification Service");
    }

    @GetMapping("/analytics")
    public ResponseEntity<ErrorResponse> analyticsFallback() {
        return buildFallback("Analytics Service");
    }

    private ResponseEntity<ErrorResponse> buildFallback(String serviceName) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder()
                        .status(503)
                        .error("Service Unavailable")
                        .message(serviceName + " is currently unavailable — please try again later")
                        .path("/fallback")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}