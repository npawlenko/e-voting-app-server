package com.github.npawlenko.evotingapp.poll.dto;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PollResponse(
        Long id,
        String question,
        LocalDateTime createdAt,
        LocalDateTime closesAt,
        boolean isPublic,
        UserResponse creator,
        List<PollAnswerResponse> pollAnswers,
        List<VoteResponse> votes
) {
}
