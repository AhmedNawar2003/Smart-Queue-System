package com.smartqueue.user_service.dto;

import com.smartqueue.user_service.model.Role;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        UUID authUserId,
        String fullName,
        String email,
        String phone,
        String nationalId,
        String governorate,
        String photoUrl,
        Role role,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}