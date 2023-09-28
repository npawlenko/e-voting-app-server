package com.github.npawlenko.evotingapp.vote.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;

import java.time.LocalDateTime;

public record VoteResponse(
        Long id,
        @JsonProperty("casted_at")
        LocalDateTime castedAt,
        PollResponse poll,
        PollAnswerResponse answer
) {
}
