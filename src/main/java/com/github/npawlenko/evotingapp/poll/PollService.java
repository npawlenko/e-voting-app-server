package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.model.User;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import com.github.npawlenko.evotingapp.utils.AuthenticatedUserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PollService {

    private final AuthenticatedUserUtility authenticatedUserUtility;
    private final PollMapper pollMapper;
    private final PollRepository pollRepository;

    public List<PollResponse> accessibleForUserPolls() {
        User user = authenticatedUserUtility.getLoggedUser();
        return pollRepository.findAccessibleForUserPolls(user.getId()).stream()
                .map(pollMapper::pollToPollResponse)
                .toList();
    }

    public List<PollResponse> userPolls() {
        User user = authenticatedUserUtility.getLoggedUser();
        return pollRepository.findPollByCreatorId(user.getId()).stream()
                .map(pollMapper::pollToPollResponse)
                .toList();
    }

    public PollResponse createPoll(PollRequest pollRequest) {
        User user = authenticatedUserUtility.getLoggedUser();

        Poll poll = buildPollFromPollRequest(pollRequest, user);
        Poll savedPoll = pollRepository.save(poll);

        return pollMapper.pollToPollResponse(savedPoll);
    }

    private static Poll buildPollFromPollRequest(PollRequest pollRequest, User user) {
        return Poll.builder()
                .question(pollRequest.getQuestion())
                .createdAt(LocalDateTime.now())
                .closesAt(pollRequest.getClosesAt())
                .isPublic(pollRequest.isPublic())
                .creator(user)
                .build();
    }
}
