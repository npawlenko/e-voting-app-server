package com.github.npawlenko.evotingapp.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import java.util.UUID;

public class TokenUtility {

    public static String generateSecureToken() {
        return Base64.encodeBase64URLSafeString(UUID.randomUUID().toString().getBytes());
    }
}
