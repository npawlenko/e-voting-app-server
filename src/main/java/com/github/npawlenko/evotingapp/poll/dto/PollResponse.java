package com.github.npawlenko.evotingapp.poll.dto;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PollResponse {
    private Long id;
    private String question;
    private LocalDateTime createdAt;
    private LocalDateTime closesAt;
    private boolean isPublic;
    private UserResponse creator;
    private List<PollAnswerResponse> pollAnswers;
    private List<VoteResponse> votes;
}
