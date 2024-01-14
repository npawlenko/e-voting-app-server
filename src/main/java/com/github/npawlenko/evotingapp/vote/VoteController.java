package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.security.auth.aspect.PublicEndpoint;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @MutationMapping("insert_vote")
    public VoteResponse createVote(@Argument("poll_answer_id") Long pollAnswerId) {
        return voteService.createVote(pollAnswerId);
    }

    @PublicEndpoint
    @MutationMapping("insert_vote_by_token")
    public VoteResponse createVoteByToken(@Argument("poll_answer_id") Long pollAnswerId, @Argument("vote_token") String token) {
        return voteService.createVoteByToken(pollAnswerId, token);
    }

    @MutationMapping("delete_vote")
    public void deleteVote(@Argument("id") Long voteId) {
        voteService.deleteVote(voteId);
    }
}
