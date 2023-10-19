package com.github.npawlenko.evotingapp.token;

import com.github.npawlenko.evotingapp.model.Token;
import com.github.npawlenko.evotingapp.security.auth.dto.TokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class TokenMapper {
    @Mapping(source = "accessToken", target = "access_token")
    public abstract TokenResponse tokenToTokenResponse(Token token);
}
