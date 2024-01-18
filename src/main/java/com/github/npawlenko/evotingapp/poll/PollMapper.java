package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.VoteToken;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.pollAnswer.PollAnswerRepository;
import com.github.npawlenko.evotingapp.user.UserMapper;
import com.github.npawlenko.evotingapp.usergroup.UserGroupMapper;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.vote.VoteMapper;
import com.github.npawlenko.evotingapp.vote.VoteRepository;
import com.github.npawlenko.evotingapp.vote.VoteService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {UserMapper.class, UserGroupMapper.class, VoteMapper.class}, builder = @Builder(disableBuilder = true))
public abstract class PollMapper {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthenticatedUserUtility authenticatedUserUtility;
    @Autowired
    private VoteService voteService;

    @Mapping(source = "pollAnswers", target = "answers")
    @Mapping(target = "systemUsers", source = "userGroup.users")
    @Mapping(target = "isPublic", source = "public")
    public abstract PollResponse pollToPollResponse(Poll poll);

    @Mapping(source = "pollAnswers", target = "answers")
    @Mapping(target = "systemUsers", source = "userGroup.users")
    @Mapping(target = "isPublic", source = "public")
    public abstract PollResponse pollToPollResponseWithToken(Poll poll, @Context VoteToken voteToken);

    @AfterMapping
    public void afterPollResponseWithTokenMapping(Poll poll, @MappingTarget PollResponse pollResponse, @Context VoteToken voteToken) {
        pollResponse.setVotePlaced(voteService.voteOnPollByTokenExists(voteToken, poll));
    }

    @Mapping(target = "public", source = "isPublic")
    @Mapping(target = "pollAnswers", source = "answers")
    public abstract void updatePoll(@MappingTarget Poll poll, PollRequest pollRequest);

    @AfterMapping
    protected void mapVotePlacedWithToken(Poll poll, @MappingTarget PollResponse pollResponse) {
        authenticatedUserUtility.getLoggedUser().ifPresent(currentUser -> {
            boolean votePlaced = voteRepository.findByVoterAndPoll(currentUser, poll).isPresent();
            pollResponse.setVotePlaced(votePlaced);
        });
    }

    @AfterMapping
    protected void mapVotePlacedWithToken(Poll poll, @MappingTarget PollResponse pollResponse, @Context VoteToken voteToken) {
        boolean votePlaced = voteRepository.findByNonSystemAccountEmailAndPoll(voteToken.getEmail(), poll).isPresent();
        pollResponse.setVotePlaced(votePlaced);
    }

    @Mapping(target = "pollAnswers", source = "answers")
    @Mapping(target = "public", source = "isPublic")
    public abstract Poll pollRequestToPoll(PollRequest pollRequest);

    @AfterMapping
    protected void afterPollMapping(@MappingTarget Poll poll) {
        poll.getPollAnswers().forEach(answer -> answer.setPoll(poll));
    }
}
