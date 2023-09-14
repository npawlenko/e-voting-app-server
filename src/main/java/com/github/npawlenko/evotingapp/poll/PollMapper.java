package com.github.npawlenko.evotingapp.poll;

import com.github.npawlenko.evotingapp.model.Poll;
import com.github.npawlenko.evotingapp.poll.dto.PollRequest;
import com.github.npawlenko.evotingapp.poll.dto.PollResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class PollMapper {
    @Mapping(source = "creator.role.role", target = "creator.role.name")
    public abstract PollResponse pollToPollResponse(Poll poll);

    public abstract Poll pollRequestToPoll(PollRequest pollRequest);
}
