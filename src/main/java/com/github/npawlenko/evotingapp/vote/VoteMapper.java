package com.github.npawlenko.evotingapp.vote;

import com.github.npawlenko.evotingapp.model.Vote;
import com.github.npawlenko.evotingapp.pollAnswer.PollAnswerMapper;
import com.github.npawlenko.evotingapp.vote.dto.VoteResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { PollAnswerMapper.class })
public abstract class VoteMapper {

    public abstract VoteResponse voteToVoteResponse(Vote vote);
}
