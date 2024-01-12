package com.github.npawlenko.evotingapp.poll.dto;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollRequest {
        @NotBlank
        private String question;
        @Future
        private LocalDateTime closesAt;
        private List<String> nonSystemUsersEmails;
        private List<Long> systemUsers;
        private Boolean isPublic;
        private List<PollAnswerRequest> answers;
}