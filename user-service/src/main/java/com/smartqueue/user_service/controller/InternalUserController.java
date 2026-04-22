package com.smartqueue.user_service.controller;

import com.smartqueue.user_service.dto.UserResponse;
import com.smartqueue.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/internal")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final UserService userService;

    @PostMapping("/create-profile")
    public ResponseEntity<UserResponse> createProfile(
           @RequestBody CreateProfileRequest request) {

        log.info("Internal: creating profile for {}", request.email());

        UserResponse response = userService.createProfile(
                UUID.fromString(request.authUserId()),
                request.email(),
                request.fullName(),
                request.phone()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    record CreateProfileRequest(
            String authUserId,
            String email,
            String fullName,
            String phone
    ) {}
}