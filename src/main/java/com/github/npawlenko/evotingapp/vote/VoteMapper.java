package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.model.Vote;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import org.mapstruct.Mapper;

@Mapper
public abstract class VoteMapper {

    public abstract VoteResponse voteToVoteResponse(Vote vote);
}
