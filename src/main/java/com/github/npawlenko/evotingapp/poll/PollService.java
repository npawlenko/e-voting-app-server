package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason;
import com.github.npawlenko.evotingapp.model.*;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import com.github.npawlenko.evotingapp.utils.AuthorizationUtility;
import com.github.npawlenko.evotingapp.utils.EmailUtility;
import com.github.npawlenko.evotingapp.utils.TokenUtility;
import com.github.npawlenko.evotingapp.voteToken.VoteTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${application.base-url}")
    private String baseUrl;

    @Transactional
    public List<PollResponse> accessibleForUserPolls(int pageSize, int pageNumber) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findAccessibleForUserPolls(currentUser.getId(), pageable).stream()
                .map(pollMapper::pollToPollResponse)
                .sorted(Comparator.comparing(PollResponse::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public List<PollResponse> userPolls(int pageSize, int pageNumber) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findPollByCreatorId(currentUser.getId(), pageable).stream()
                .map(pollMapper::pollToPollResponse)
                .sorted(Comparator.comparing(PollResponse::getCreatedAt).reversed())
                .toList();
    }

    @Transactional
    public PollResponse createPoll(PollRequest pollRequest) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));

        Poll poll = pollMapper.pollRequestToPoll(pollRequest);
        poll.setCreatedAt(LocalDateTime.now());
        poll.setClosesAt(pollRequest.getClosesAt());
        poll.setCreator(currentUser);
        createAndSetUserGroup(pollRequest, currentUser, poll);
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
                    String.format(
                            "Zostałeś dodany do głosowania w ankiecie \"%s\". Oddaj głos: %s/poll/%d/%s",
                            poll.getQuestion(), baseUrl, poll.getId(), token));
        });

        return pollMapper.pollToPollResponse(poll);
    }


    @Transactional
    public PollResponse updatePoll(Long pollId, PollRequest pollRequest) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, poll.getCreator());

        pollMapper.updatePoll(poll, pollRequest);
        createAndSetUserGroup(pollRequest, currentUser, poll);
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
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, poll.getCreator());

        poll.setClosesAt(LocalDateTime.now());
        pollRepository.save(poll);
    }

    @Transactional
    public void deletePoll(Long pollId) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        Poll pollBeforeUpdate = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        authorizationUtility.requireAdminOrOwnerPermission(currentUser, pollBeforeUpdate.getCreator());

        pollRepository.deleteById(pollId);
    }

    @Transactional
    public PollResponse findPollById(Long pollId) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        Poll poll = pollRepository.findById(pollId).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if (!poll.isPublic() && !poll.getCreator().equals(currentUser) && !userInPollUserGroup(currentUser, poll) && !RoleType.ADMIN.equals(currentUser.getRole().getRole())) {
            throw new ApiRequestException(FORBIDDEN);
        }
        return pollMapper.pollToPollResponse(poll);
    }

    @Transactional
    public PollResponse findPollByIdUsingToken(Long pollId, String token) {
        VoteToken voteToken = voteTokenRepository.findByToken(token).orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if(!voteToken.getPoll().getId().equals(pollId)) {
            throw new IllegalArgumentException("Invalid poll id");
        }
        PollResponse pollResponse = pollMapper.pollToPollResponseWithToken(voteToken.getPoll(), voteToken);
        return pollResponse;
    }

    private boolean userInPollUserGroup(User user, Poll poll) {
        return Optional.ofNullable(poll.getUserGroup()).map(ug -> ug.getUsers().contains(user)).orElse(false);
    }

    public List<PollResponse> findAllPolls(int pageSize, int pageNumber) {
        User currentUser = authenticatedUserUtility.getLoggedUser().orElseThrow(() -> new ApiRequestException(ApiRequestExceptionReason.USER_NOT_LOGGED_IN));
        if(!RoleType.ADMIN.equals(currentUser.getRole().getRole())) {
            throw new ApiRequestException(FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return pollRepository.findAll(pageable).stream().map(pollMapper::pollToPollResponse).toList();
    }
}
