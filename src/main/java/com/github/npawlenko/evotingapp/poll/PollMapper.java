package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PollMapper {
    @Mapping(source = "poll.creator.role.role", target = "poll.creator.role.name")
    PollResponse pollToPollResponse(Poll poll);
}
