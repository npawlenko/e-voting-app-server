package com.github.npawlenko.evotingapp.poll.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record PollRequest(
        @NotBlank
        String question,
        @Future
        LocalDateTime closesAt,
        List<String> nonSystemUsersEmails,
        boolean isPublic
) {
}