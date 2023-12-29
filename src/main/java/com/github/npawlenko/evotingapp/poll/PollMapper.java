package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.vote.VoteRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PollMapper {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthenticatedUserUtility authenticatedUserUtility;

    @Mapping(source = "pollAnswers", target = "answers")
    @Mapping(source = "creator.role.role", target = "creator.role.name")
    public abstract PollResponse pollToPollResponse(Poll poll);

    @AfterMapping
    protected void mapRolesInUserList(Poll poll, @MappingTarget PollResponse pollResponse) {
        boolean votePlaced = voteRepository.findByVoterAndPoll(authenticatedUserUtility.getLoggedUser(), poll).isPresent();
        pollResponse.setVotePlaced(votePlaced);
    }

    public abstract Poll pollRequestToPoll(PollRequest pollRequest);
}
