package com.smartqueue.queueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateQueueRequest {

    @NotNull(message = "Center ID is required")
    private UUID centerId;

    @NotBlank(message = "Center name is required")
    private String centerName;

    @Positive(message = "Avg service minutes must be positive")
    private int avgServiceMinutes = 5;
}