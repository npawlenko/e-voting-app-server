package com.github.npawlenko.evotingapp.utils;

import com.github.npawlenko.evotingapp.exception.ApiRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.github.npawlenko.evotingapp.exception.ApiRequestExceptionReason.TOKEN_MISSING;

@Component
public class HttpUtility {

    public static HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public String getAuthorizationToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiRequestException(TOKEN_MISSING);
        }
        return authorizationHeader.substring(7);
    }
}
