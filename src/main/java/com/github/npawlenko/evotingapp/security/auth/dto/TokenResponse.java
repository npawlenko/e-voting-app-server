package com.github.npawlenko.evotingapp.security.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
