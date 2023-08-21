package com.github.npawlenko.evotingapp.security.auth.aspect;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.security.JwtService;
import com.github.npawlenko.evotingapp.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Aspect
@Component
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;

    @Pointcut("@annotation(org.springframework.graphql.data.method.annotation.QueryMapping)")
    public void queryMapping() {
    }

    @Pointcut("@annotation(org.springframework.graphql.data.method.annotation.MutationMapping)")
    public void mutationMapping() {
    }

    @Pointcut("queryMapping() || mutationMapping()")
    public void graphQlMapping() {
    }

    @Pointcut("@annotation(com.github.npawlenko.evotingapp.security.auth.aspect.PublicEndpoint)")
    public void publicEndpoint() {
    }


    @Around("graphQlMapping() && !publicEndpoint()")
    public Object aroundNotPublicEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentRequest();

        Authentication authentication = getAuthentication();
        if (authentication != null) {
            return joinPoint.proceed();
        }

        String token = getAuthorizationToken(request);
        try {
            Jwt decodedToken = jwtService.decodeJwt(token);
            if (isTokenExpired(decodedToken)) {
                throw new ApiRequestException("Expired token", HttpStatus.UNAUTHORIZED);
            }

            tokenRepository.findByAccessToken(token).
                    orElseThrow(() -> new ApiRequestException("Authentication error", HttpStatus.UNAUTHORIZED));
            UserDetails userDetails = userDetailsService.loadUserByUsername(decodedToken.getSubject());
            if (!userDetails.getUsername().equals(decodedToken.getSubject())) {
                throw new ApiRequestException("Authentication error", HttpStatus.UNAUTHORIZED);
            }
            authenticateUser(request, userDetails);
        } catch (JwtException e) {
            throw new ApiRequestException("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        return joinPoint.proceed();
    }

    private boolean isTokenExpired(Jwt decodedToken) {
        Instant expiresAt;
        return (expiresAt = decodedToken.getExpiresAt()) != null
                && expiresAt.isAfter(Instant.now());
    }

    private String getAuthorizationToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiRequestException("Missing token", HttpStatus.UNAUTHORIZED);
        }
        return authorizationHeader.substring(authorizationHeader.lastIndexOf("Bearer "));
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private void authenticateUser(HttpServletRequest request, UserDetails userDetails) {
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
}
