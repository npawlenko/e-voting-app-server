package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.user.UserMapper;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.usergroup.UserGroupMapper;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.vote.VoteRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class, UserGroupMapper.class}, builder = @Builder(disableBuilder = true))
public abstract class PollMapper {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthenticatedUserUtility authenticatedUserUtility;
    @Autowired
    private UserRepository userRepository;

    @Mapping(source = "pollAnswers", target = "answers")
    public abstract PollResponse pollToPollResponse(Poll poll);

    @AfterMapping
    protected void mapRolesInUserList(Poll poll, @MappingTarget PollResponse pollResponse) {
        boolean votePlaced = voteRepository.findByVoterAndPoll(authenticatedUserUtility.getLoggedUser(), poll).isPresent();
        pollResponse.setVotePlaced(votePlaced);
    }

    @Mapping(target = "pollAnswers", source = "answers")
    public abstract Poll pollRequestToPoll(PollRequest pollRequest);

    @AfterMapping
    protected void afterPollMapping(PollRequest pollRequest, @MappingTarget Poll poll) {
        poll.getPollAnswers().forEach(answer -> answer.setPoll(poll));
    }
}
