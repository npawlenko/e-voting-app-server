package com.github.npawlenko.evotingapp.pollAnswer.dto;

import jakarta.validation.constraints.NotBlank;

public record PollAnswerRequest(
        @NotBlank String answer
) {
}
