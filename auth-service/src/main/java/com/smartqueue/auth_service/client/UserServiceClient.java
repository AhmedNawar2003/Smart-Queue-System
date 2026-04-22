package com.smartqueue.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserServiceClient {

    @PostMapping("/internal/create-profile")
    void createProfile(@RequestBody CreateProfileRequest request);

    record CreateProfileRequest(
            String authUserId,
            String email,
            String fullName,
            String phone
    ) {}
}