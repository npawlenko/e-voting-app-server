package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.model.UserGroup;
import com.github.npawlenko.evotingapp.model.VoteToken;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import com.github.npawlenko.evotingapp.utils.EmailUtility;
import com.github.npawlenko.evotingapp.utils.TokenUtility;
import com.github.npawlenko.evotingapp.voteToken.VoteTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
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
    private final UserRepository userRepository;

    @Transactional
    public List<PollResponse> accessibleForUserPolls(int pageSize, int pageNumber) {
        User user = authenticatedUserUtility.getLoggedUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findAccessibleForUserPolls(user.getId(), pageable).stream()
                .map(pollMapper::pollToPollResponse)
                .sorted(Comparator.comparing(PollResponse::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public List<PollResponse> userPolls(int pageSize, int pageNumber) {
        User user = authenticatedUserUtility.getLoggedUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findPollByCreatorId(user.getId(), pageable).stream()
                .map(pollMapper::pollToPollResponse)
                .sorted(Comparator.comparing(PollResponse::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public PollResponse createPoll(PollRequest pollRequest) {
        User user = authenticatedUserUtility.getLoggedUser();

        Poll poll = pollMapper.pollRequestToPoll(pollRequest);
        poll.setCreatedAt(LocalDateTime.now());
        poll.setClosesAt(pollRequest.getClosesAt());
        poll.setCreator(user);
        createAndSetUserGroup(pollRequest, user, poll);
        pollRepository.save(poll);

        pollRequest.getNonSystemUsersEmails().forEach(email -> {
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

        return pollMapper.pollToPollResponse(poll);
    }


    @Transactional
    public PollResponse updatePoll(Long pollId, PollRequest pollRequest) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, poll.getCreator());

        pollMapper.updatePoll(poll, pollRequest);
        createAndSetUserGroup(pollRequest, user, poll);
        pollRepository.save(poll);

        return pollMapper.pollToPollResponse(poll);
    }

    private void createAndSetUserGroup(PollRequest pollRequest, User user, Poll poll) {
        UserGroup ug = new UserGroup();
        List<User> users = userRepository.findAllById(pollRequest.getSystemUsers());
        ug.setUsers(users);
        ug.setPolls(List.of(poll));
        ug.setOwner(user);
        ug.setName("Unnamed");
        poll.setUserGroup(ug);
    }

    @Transactional
    public void closePoll(Long pollId) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, poll.getCreator());

        poll.setClosesAt(LocalDateTime.now());
        pollRepository.save(poll);
    }

    @Transactional
    public void deletePoll(Long pollId) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll pollBeforeUpdate = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(user, pollBeforeUpdate.getCreator());

        pollRepository.deleteById(pollId);
    }

    @Transactional
    public PollResponse findPollById(Long pollId) {
        User user = authenticatedUserUtility.getLoggedUser();
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if (!poll.getCreator().equals(user) && !userInPollUserGroup(user, poll)) {
            throw new ApiRequestException(FORBIDDEN);
        }
        return pollMapper.pollToPollResponse(poll);
    }

    @Transactional
    public PollResponse findPollByIdUsingToken(String token) {
        PollResponse pollResponse = new PollResponse();
        VoteToken voteToken = voteTokenRepository.findByToken(token).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        pollMapper.pollToPollResponseWithToken(voteToken.getPoll(), pollResponse, voteToken);
        return pollResponse;
    }

    private boolean userInPollUserGroup(User user, Poll poll) {
        return Optional.ofNullable(poll.getUserGroup()).map(ug -> ug.getUsers().contains(user)).orElse(false);
    }
}
