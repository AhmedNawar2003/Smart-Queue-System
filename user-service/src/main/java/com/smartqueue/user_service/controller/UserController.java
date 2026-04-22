package com.smartqueue.user_service.controller;

import com.smartqueue.user_service.dto.ChangeRoleRequest;
import com.smartqueue.user_service.dto.UpdateProfileRequest;
import com.smartqueue.user_service.dto.UserResponse;
import com.smartqueue.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
@SecurityRequirement(name = "Bearer Auth")
public class UserController {

    private final UserService userService;

    // ── My profile ───────────────────────────────────────────
    @GetMapping("/me")
    @Operation(summary = "Get my profile")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.getMyProfile(email));
    }

    @PutMapping("/me")
    @Operation(summary = "Update my profile")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }

    // ── Admin endpoints ──────────────────────────────────────
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users — Admin only")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get user by ID — Admin/Staff only")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change user role — Admin only")
    public ResponseEntity<UserResponse> changeRole(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeRoleRequest request) {
        return ResponseEntity.ok(userService.changeRole(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete user — Admin only")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}