package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @MutationMapping("createVote")
    public VoteResponse createVote(@Argument("pollAnswerId") Long pollAnswerId) {
        return voteService.createVote(pollAnswerId);
    }

    @MutationMapping("deleteVote")
    public void deleteVote(@Argument("voteId") Long voteId) {
        voteService.deleteVote(voteId);
    }
}
