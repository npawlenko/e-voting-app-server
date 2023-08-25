package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    @QueryMapping("polls")
    public List<PollResponse> findAccessibleForUserPolls() {
        return pollService.accessibleForUserPolls();
    }

    @QueryMapping("userPolls")
    public List<PollResponse> findUserPolls() {
        return pollService.userPolls();
    }

    @MutationMapping("createPoll")
    public PollResponse createPoll(@Valid @Argument("poll") PollRequest pollRequest) {
        return pollService.createPoll(pollRequest);
    }
}
