package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.security.auth.aspect.PublicEndpoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
            @Min(1) @Argument("page_size") int pageSize,
            @Min(0) @Argument("page_number") int pageNumber
    ) {
        return pollService.accessibleForUserPolls(pageSize, pageNumber);
    }

    @QueryMapping("poll")
    public PollResponse findPollById(@Argument("poll_id") Long pollId) {
        return pollService.findPollById(pollId);
    }

    @PublicEndpoint
    @QueryMapping("poll_by_token")
    public PollResponse findByPollByIdUsingToken(@Argument("vote_token") String token) {
        return pollService.findPollByIdUsingToken(token);
    }

    @QueryMapping("user_polls")
    public List<PollResponse> findUserPolls(
            @Min(1) @Argument("page_size") int pageSize,
            @Min(0) @Argument("page_number") int pageNumber
    ) {
        return pollService.userPolls(pageSize, pageNumber);
    }

    @MutationMapping("insert_poll")
    public PollResponse createPoll(@Valid @Argument("poll") PollRequest pollRequest) {
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
