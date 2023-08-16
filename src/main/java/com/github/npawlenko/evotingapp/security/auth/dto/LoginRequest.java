package com.github.npawlenko.evotingapp.security.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email
    private String email;
    @NotBlank
    private String password;
}
