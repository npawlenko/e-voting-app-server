package com.github.npawlenko.evotingapp.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.npawlenko.evotingapp.role.dto.RoleResponse;
import lombok.*;

public record UserResponse(
        Long id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        @With
        RoleResponse role
) {
}
