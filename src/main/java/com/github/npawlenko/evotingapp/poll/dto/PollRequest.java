package com.github.npawlenko.evotingapp.poll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record PollRequest(
        @NotBlank
        String question,
        @Future
        @JsonProperty("closes_at")
        LocalDateTime closesAt,
        @JsonProperty("public")
        boolean isPublic
) {
}