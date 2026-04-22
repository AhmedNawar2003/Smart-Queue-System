package com.smartqueue.user_service.service;

import com.smartqueue.user_service.dto.ChangeRoleRequest;
import com.smartqueue.user_service.dto.UpdateProfileRequest;
import com.smartqueue.user_service.dto.UserResponse;
import com.smartqueue.user_service.exception.ResourceNotFoundException;
import com.smartqueue.user_service.model.UserProfile;
import com.smartqueue.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserProfileRepository repository;

    // ── Get my profile ───────────────────────────────────────
    public UserResponse getMyProfile(String email) {
        UserProfile profile = repository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found for: " + email));
        return toResponse(profile);
    }

    // ── Get by ID ────────────────────────────────────────────
    public UserResponse getById(UUID id) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + id));
        return toResponse(profile);
    }

    // ── Get all (paginated) ──────────────────────────────────
    public Page<UserResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(this::toResponse);
    }

    // ── Update profile ───────────────────────────────────────
    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        UserProfile profile = repository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Profile not found for: " + email));

        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setNationalId(request.getNationalId());
        profile.setGovernorate(request.getGovernorate());
        profile.setPhotoUrl(request.getPhotoUrl());

        repository.save(profile);
        log.info("Profile updated for: {}", email);
        return toResponse(profile);
    }

    // ── Change role (admin only) ─────────────────────────────
    @Transactional
    public UserResponse changeRole(UUID id, ChangeRoleRequest request) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + id));

        profile.setRole(request.getRole());
        repository.save(profile);
        log.info("Role changed to {} for user: {}", request.getRole(), id);
        return toResponse(profile);
    }

    // ── Delete (admin only) ──────────────────────────────────
    @Transactional
    public void deleteUser(UUID id) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found: " + id));
        profile.setActive(false);   // Soft delete
        repository.save(profile);
        log.info("User soft-deleted: {}", id);
    }

    // ── Create profile (called internally after register) ────
    @Transactional
    public UserResponse createProfile(
            UUID authUserId, String email,
            String fullName, String phone) {

        if (repository.existsByEmail(email)) {
            return toResponse(repository.findByEmail(email).get());
        }

        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .email(email)
                .fullName(fullName)
                .phone(phone)
                .build();

        repository.save(profile);
        log.info("Profile created for: {}", email);
        return toResponse(profile);
    }

    // ── Mapper ───────────────────────────────────────────────
    private UserResponse toResponse(UserProfile p) {
        return UserResponse.builder()
                .id(p.getId())
                .authUserId(p.getAuthUserId())
                .fullName(p.getFullName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .nationalId(p.getNationalId())
                .governorate(p.getGovernorate())
                .photoUrl(p.getPhotoUrl())
                .role(p.getRole())
                .active(p.isActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}