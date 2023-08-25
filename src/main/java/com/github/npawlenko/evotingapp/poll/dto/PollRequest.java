package com.github.npawlenko.evotingapp.poll.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PollRequest {
    @NotBlank
    private String question;
    @Future
    private LocalDateTime closesAt;
    private boolean isPublic;
}
