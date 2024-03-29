package com.github.npawlenko.evotingapp.security;

import com.github.npawlenko.evotingapp.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${application.jwt.access-token-expiration-seconds}")
    private long accessTokenExpiration;
    @Value("${application.jwt.refresh-token-expiration-seconds}")
    private long refreshTokenExpiration;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public Jwt generateJwtAccessToken(User user) {
        return generateJwt(user, accessTokenExpiration);
    }

    public Jwt generateJwtRefreshToken(User user) {
        return generateJwt(user, refreshTokenExpiration);
    }

    private Jwt generateJwt(User user, long expiration) {
        Instant now = Instant.now();

        String scope = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiration))
                .subject(user.getUsername())
                .claim("role", scope)
                .claim("fullName", user.getFullName())
                .claim("id", user.getId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));
    }

    public Jwt decodeJwt(String token) throws JwtException {
        return jwtDecoder.decode(token);
    }
}
