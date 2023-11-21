package com.github.npawlenko.evotingapp.poll.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record PollRequest(
        @NotBlank
        String question,
        @Future
        LocalDateTime closesAt,
        boolean isPublic
) {
}