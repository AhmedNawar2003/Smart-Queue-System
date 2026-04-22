package com.smartqueue.user_service.dto;

import com.smartqueue.user_service.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}