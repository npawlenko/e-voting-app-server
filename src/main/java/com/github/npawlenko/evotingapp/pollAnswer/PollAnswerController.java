package com.github.npawlenko.evotingapp.pollAnswer;

import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerRequest;
import com.github.npawlenko.evotingapp.pollAnswer.dto.PollAnswerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PollAnswerController {

    private final PollAnswerService pollAnswerService;

    @MutationMapping("createPollAnswer")
    public PollAnswerResponse createPollAnswer(
            @Argument("pollId") Long pollId,
            @Argument("pollAnswer") PollAnswerRequest pollAnswer)
    {
        return pollAnswerService.createPollAnswer(pollId, pollAnswer);
    }

    @QueryMapping("pollAnswers")
    public List<PollAnswerResponse> pollAnswers(@Argument("pollId") Long pollId) {
        return pollAnswerService.pollAnswers(pollId);
    }

    @MutationMapping("updatePollAnswer")
    public PollAnswerResponse updatePollAnswer(
            @Argument("pollAnswerId") Long pollAnswerId,
            @Valid @Argument("pollAnswer") PollAnswerRequest pollAnswer
    ) {
        return pollAnswerService.updatePollAnswer(pollAnswerId, pollAnswer);
    }

    @MutationMapping("deletePollAnswer")
    public void deletePollAnswer(@Argument("pollAnswerId") Long pollAnswerId) {
        pollAnswerService.deletePollAnswer(pollAnswerId);
    }
}
