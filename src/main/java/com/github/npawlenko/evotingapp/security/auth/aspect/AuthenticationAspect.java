package com.github.npawlenko.evotingapp.security.auth.aspect;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import com.github.npawlenko.evotingapp.security.JwtService;
import com.github.npawlenko.evotingapp.token.TokenRepository;
import com.github.npawlenko.evotingapp.utils.HttpUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.*;

@Aspect
@Component
@EnableAspectJAutoProxy
@Slf4j
@RequiredArgsConstructor
public class AuthenticationAspect {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final HttpUtility httpUtility;

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
        HttpServletRequest request = HttpUtility.getCurrentRequest();

        org.springframework.security.core.Authentication authentication = getAuthentication();
        if (authentication != null) {
            return joinPoint.proceed();
        }

        String token = httpUtility.getAuthorizationToken(request);
        try {
            Jwt decodedToken = jwtService.decodeJwt(token);

            tokenRepository.findByAccessToken(token).
                    orElseThrow(() -> new ApiRequestException(AUTHENTICATION_ERROR));
            UserDetails userDetails = userDetailsService.loadUserByUsername(decodedToken.getSubject());
            authenticateUser(request, userDetails);
        } catch (JwtException e) {
            log.error(e.getMessage());
            throw new ApiRequestException(TOKEN_INVALID);
        }

        return joinPoint.proceed();
    }

    private org.springframework.security.core.Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
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
