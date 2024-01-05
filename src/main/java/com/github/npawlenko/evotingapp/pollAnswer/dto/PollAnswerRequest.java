package com.github.npawlenko.evotingapp.pollAnswer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollAnswerRequest {
    @NotBlank
    private String answer;
}
