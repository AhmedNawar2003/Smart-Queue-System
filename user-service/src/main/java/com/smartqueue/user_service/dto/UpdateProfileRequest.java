package com.smartqueue.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100)
    private String fullName;

    @Pattern(regexp = "^01[0125][0-9]{8}$",
            message = "Invalid Egyptian phone number")
    private String phone;

    private String nationalId;

    private String governorate;

    private String photoUrl;
}