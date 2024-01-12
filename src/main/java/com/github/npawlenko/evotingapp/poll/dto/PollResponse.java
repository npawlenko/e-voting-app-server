package com.github.npawlenko.evotingapp.poll.dto;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
import com.github.npawlenko.evotingapp.usergroup.dto.UserGroupResponse;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PollResponse {
        private Long id;
        private String question;
        private LocalDateTime createdAt;
        private LocalDateTime closesAt;
        private boolean isPublic;
        private UserResponse creator;
        private List<PollAnswerResponse> answers;
        private List<UserResponse> systemUsers;
        private List<VoteResponse> votes;
        private boolean votePlaced;
}