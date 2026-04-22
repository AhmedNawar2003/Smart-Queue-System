package com.smartqueue.auth_service.controller;

import com.smartqueue.auth_service.dto.AdminRegisterRequest;
import com.smartqueue.auth_service.dto.AuthResponse;
import com.smartqueue.auth_service.dto.LoginRequest;
import com.smartqueue.auth_service.dto.RegisterRequest;
import com.smartqueue.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, Login, OAuth2")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ── OAuth2 ───────────────────────────────────────────────

    @GetMapping("/oauth2/google")
    @Operation(summary = "Redirect to Google OAuth2 login")
    public ResponseEntity<Map<String, String>> googleLogin() {
        // الـ Spring Security بيتعامل مع الـ redirect أوتوماتيك
        // الـ endpoint ده بس بيوضح الـ URL للـ client
        return ResponseEntity.ok(Map.of(
                "loginUrl", "/oauth2/authorization/google",
                "description", "Redirect the user to this URL to start Google login"
        ));
    }

    @GetMapping("/oauth2/failure")
    @Operation(summary = "OAuth2 login failure callback")
    public ResponseEntity<Map<String, String>> oauthFailure() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "OAuth2 login failed"));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running ✅");
    }

    @PostMapping("/register-admin")
    @Operation(summary = "Register with any role — requires admin secret")
    public ResponseEntity<AuthResponse> registerWithRole(
            @Valid @RequestBody AdminRegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registerWithRole(request));
    }
}

