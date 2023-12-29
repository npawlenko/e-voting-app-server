package com.github.npawlenko.evotingapp.poll.dto;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import com.github.npawlenko.evotingapp.user.dto.UserResponse;
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
        private boolean votePlaced;
}