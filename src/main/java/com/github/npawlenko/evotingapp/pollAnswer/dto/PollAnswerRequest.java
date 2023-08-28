package com.github.npawlenko.evotingapp.pollAnswer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PollAnswerRequest {
    @NotBlank
    private String answer;
}
