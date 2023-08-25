package com.github.npawlenko.evotingapp.pollAnswer.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PollAnswerResponse {
    private Long id;
    private String answer;
}
