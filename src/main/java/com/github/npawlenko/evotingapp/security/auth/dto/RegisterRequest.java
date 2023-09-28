package com.github.npawlenko.evotingapp.security.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        @NotBlank
        String lastName,
        @Email String email,
        @NotBlank String password
) {
}
