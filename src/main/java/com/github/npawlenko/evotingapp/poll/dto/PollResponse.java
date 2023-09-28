package com.github.npawlenko.evotingapp.poll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PollResponse(
        Long id,
        String question,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("closes_at")
        LocalDateTime closesAt,
        @JsonProperty("public")
        boolean isPublic,
        UserResponse creator,
        @JsonProperty("answers")
        List<PollAnswerResponse> pollAnswers,
        List<VoteResponse> votes
) {
}
