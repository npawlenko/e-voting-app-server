package com.github.npawlenko.evotingapp.vote.dto;

import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {
    private Long id;
    private LocalDateTime castedAt;
    private PollResponse poll;
    private PollAnswerResponse answer;
}
