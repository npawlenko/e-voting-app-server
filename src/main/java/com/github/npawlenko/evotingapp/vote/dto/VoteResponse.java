package com.github.npawlenko.evotingapp.vote.dto;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;

import java.time.LocalDateTime;

public record VoteResponse(
        Long id,
        LocalDateTime castedAt,
        PollAnswerResponse answer
) {
}
