package com.github.npawlenko.evotingapp.security.auth;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.model.*;
import com.github.npawlenko.evotingapp.resetToken.ResetTokenRepository;
import com.github.npawlenko.evotingapp.role.RoleRepository;
import com.github.npawlenko.evotingapp.security.JwtService;
import com.github.npawlenko.evotingapp.security.auth.dto.RegisterRequest;
import com.github.npawlenko.evotingapp.security.auth.dto.TokenResponse;
import com.github.npawlenko.evotingapp.token.TokenMapper;
import com.github.npawlenko.evotingapp.token.TokenRepository;
import com.github.npawlenko.evotingapp.user.UserRepository;
import com.github.npawlenko.evotingapp.utils.EmailUtility;
import com.github.npawlenko.evotingapp.utils.HttpUtility;
import com.github.npawlenko.evotingapp.utils.TokenUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Objects;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final JwtService jwtService;
    private final HttpUtility httpUtility;
    private final EmailUtility emailUtility;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenMapper tokenMapper;
    private final TokenRepository tokenRepository;
    private final ResetTokenRepository resetTokenRepository;

    @Value("${application.security.password-reset-token-expiration-seconds}")
    private Long passwordResetTokenExpiration;


    public TokenResponse login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );
        } catch (InternalAuthenticationServiceException e) {
            throw new ApiRequestException(USER_CREDENTIALS_INVALID);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiRequestException(USER_CREDENTIALS_INVALID));
        Jwt refreshToken = jwtService.generateJwtRefreshToken(user);
        Token token = buildToken(user, refreshToken);
        tokenRepository.save(token);

        HttpServletResponse response = HttpUtility.getCurrentResponse();
        long expiresIn = Objects.requireNonNull(refreshToken.getExpiresAt()).getEpochSecond() - Instant.now().getEpochSecond();
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, token.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) expiresIn);
        response.addCookie(cookie);

        return tokenMapper.tokenToTokenResponse(token);
    }

    public TokenResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent())
            throw new ApiRequestException(USER_EMAIL_ALREADY_EXISTS);

        Role userRole = roleRepository.findByRole(RoleType.USER)
                .orElseThrow();

        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        User user = userRepository.save(User.builder()
                .email(registerRequest.email())
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .password(encodedPassword)
                .role(userRole)
                .build()
        );
        Token token = buildToken(user);
        tokenRepository.save(token);

        return tokenMapper.tokenToTokenResponse(token);
    }

    public void logout() {
        HttpServletRequest request = HttpUtility.getCurrentRequest();
        String accessToken = httpUtility.getAuthorizationToken(request);

        Token token = tokenRepository.findByAccessToken(accessToken)
                .orElseThrow();
        tokenRepository.delete(token);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public TokenResponse refresh() {
        HttpServletRequest request = HttpUtility.getCurrentRequest();
        if (request.getCookies() == null) {
            throw new ApiRequestException(TOKEN_MISSING);
        }

        Cookie refreshCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .orElseThrow(() -> new ApiRequestException(TOKEN_MISSING));
        String refreshToken = refreshCookie.getValue();
        try {
            Jwt decodedToken = jwtService.decodeJwt(refreshToken);
            Token token = tokenRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new ApiRequestException(TOKEN_INVALID));
            User user = token.getUser();

            if (!user.getUsername().equals(decodedToken.getSubject())) {
                log.error("Username does not match token subject. {} differs from {}", user.getUsername(), decodedToken.getSubject());
                throw new ApiRequestException(AUTHENTICATION_ERROR);
            }
            authorizeUser(request, user);

            Token newToken = buildToken(user);
            tokenRepository.delete(token);
            tokenRepository.save(newToken);

            return tokenMapper.tokenToTokenResponse(newToken);
        } catch (JwtException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new ApiRequestException(TOKEN_INVALID);
        }
    }

    private void authorizeUser(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private Token buildToken(User user, Jwt refreshToken) {
        Jwt accessToken = jwtService.generateJwtAccessToken(user);
        return Token.builder()
                .user(user)
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresAt(
                        instantToLocalDateTime(
                                Objects.requireNonNull(refreshToken.getExpiresAt())
                        )
                )
                .build();
    }

    private Token buildToken(User user) {
        Jwt accessToken = jwtService.generateJwtAccessToken(user);
        Jwt refreshToken = jwtService.generateJwtRefreshToken(user);
        return Token.builder()
                .user(user)
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .expiresAt(
                        instantToLocalDateTime(
                                Objects.requireNonNull(refreshToken.getExpiresAt())
                        )
                )
                .build();
    }

    public void resetPassword(String newPassword, String token) {
        ResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiRequestException(FORBIDDEN);
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        User user = resetToken.getUser();
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }


    public void resetPasswordSendLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND));
        String token = TokenUtility.generateSecureToken();
        ResetToken resetToken = ResetToken.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(passwordResetTokenExpiration))
                .build();
        resetTokenRepository.save(resetToken);
        emailUtility.sendSimpleMessage(email,
                "Resetowanie hasłą",
                String.format("Zresetuj hasło używając tokena %s", token));
    }

    private LocalDateTime instantToLocalDateTime(Instant instant) {
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
