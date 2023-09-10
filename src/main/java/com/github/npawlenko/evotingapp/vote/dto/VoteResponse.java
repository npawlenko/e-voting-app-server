package com.github.npawlenko.evotingapp.vote.dto;

import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;

import java.time.LocalDateTime;

public record VoteResponse(
        Long id,
        LocalDateTime castedAt,
        PollResponse poll,
        PollAnswerResponse answer
) {
}
