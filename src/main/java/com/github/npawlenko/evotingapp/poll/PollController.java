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
    public List<PollResponse> findAccessibleForUserPolls(
            @Argument("limit") Integer limit,
            @Argument("offset") Integer offset
    ) {
        return pollService.accessibleForUserPolls();
    }

    @QueryMapping("user_polls")
    public List<PollResponse> findUserPolls(
            @Argument("limit") Integer limit,
            @Argument("offset") Integer offset
    ) {
        return pollService.userPolls();
    }

    @MutationMapping("insert_poll")
    public PollResponse createPoll(@Valid @Argument("object") PollRequest pollRequest) {
        return pollService.createPoll(pollRequest);
    }

    @MutationMapping("update_poll")
    public PollResponse updatePoll(
            @Argument("id") Long pollId,
            @Valid @Argument("object") PollRequest pollRequest
    ) {
        return pollService.updatePoll(pollId, pollRequest);
    }

    @MutationMapping("delete_poll")
    public void deletePoll(@Argument("id") Long pollId) {
        pollService.deletePoll(pollId);
    }
}
