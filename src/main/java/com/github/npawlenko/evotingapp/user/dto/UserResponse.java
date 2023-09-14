package com.github.npawlenko.evotingapp.user.dto;

import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import lombok.*;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        @With
        RoleResponse role
) {
}
