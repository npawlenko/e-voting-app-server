package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.VoteToken;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import com.github.npawlenko.evotingapp.utils.EmailUtility;
import com.github.npawlenko.evotingapp.utils.TokenUtility;
import com.github.npawlenko.evotingapp.voteToken.VoteTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.FORBIDDEN;
import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PollService {

    private final AuthenticatedUserUtility authenticatedUserUtility;
    private final AuthorizationUtility authorizationUtility;
    private final EmailUtility emailUtility;
    private final PollMapper pollMapper;
    private final PollRepository pollRepository;
    private final VoteTokenRepository voteTokenRepository;

    public List<PollResponse> accessibleForUserPolls(int pageSize, int pageNumber) {
        User user = authenticatedUserUtility.getLoggedUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findAccessibleForUserPolls(user.getId(), pageable).stream().map(pollMapper::pollToPollResponse).toList();
    }

    public List<PollResponse> userPolls(int pageSize, int pageNumber) {
        User user = authenticatedUserUtility.getLoggedUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findPollByCreatorId(user.getId(), pageable).stream().map(pollMapper::pollToPollResponse).toList();
    }

    public PollResponse createPoll(PollRequest pollRequest) {
        User user = authenticatedUserUtility.getLoggedUser();

        Poll poll = pollMapper.pollRequestToPoll(pollRequest);
        poll.setCreatedAt(LocalDateTime.now());
        poll.setCreator(user);
        Poll savedPoll = pollRepository.save(poll);

        pollRequest.nonSystemUsersEmails().forEach(email -> {
            String token = TokenUtility.generateSecureToken();
            VoteToken voteToken = VoteToken.builder()
                    .poll(poll)
                    .token(token)
                    .email(email)
                    .build();
            voteTokenRepository.save(voteToken);
            emailUtility.sendSimpleMessage(email,
                    "Dodano cię do głosowania",
                    String.format("Zagłosuj używając tokena %s", token));
        });

        return pollMapper.pollToPollResponse(savedPoll);
    }


    public PollResponse updatePoll(Long pollId, PollRequest pollRequest) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll pollBeforeUpdate = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, pollBeforeUpdate.getCreator());

        Poll poll = pollMapper.pollRequestToPoll(pollRequest);
        poll.setCreator(pollBeforeUpdate.getCreator());
        Poll savedPollAnswer = pollRepository.save(poll);

        return pollMapper.pollToPollResponse(savedPollAnswer);
    }

    public void deletePoll(Long pollId) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll pollBeforeUpdate = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, pollBeforeUpdate.getCreator());

        pollRepository.deleteById(pollId);
    }

    public PollResponse findPollById(Long pollId) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if (!poll.getCreator().equals(user) && !userInPollUserGroup(user, poll)) {
            throw new ApiRequestException(FORBIDDEN);
        }
        return pollMapper.pollToPollResponse(poll);
    }

    public PollResponse findPollByIdUsingToken(String token) {
        VoteToken voteToken = voteTokenRepository.findByToken(token).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        return pollMapper.pollToPollResponse(voteToken.getPoll());
    }

    private boolean userInPollUserGroup(User user, Poll poll) {
        return Optional.ofNullable(poll.getUserGroup()).map(ug -> ug.getUsers().contains(user)).orElse(false);
    }
}
