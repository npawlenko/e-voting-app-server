package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.model.Vote;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class VoteMapper {

    @Mapping(source = "poll.creator.role.role", target = "poll.creator.role.name")
    public abstract VoteResponse voteToVoteResponse(Vote vote);
}
