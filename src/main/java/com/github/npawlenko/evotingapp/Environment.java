package com.github.npawlenko.evotingapp;

public enum Environment {

    DEVELOPMENT("development"),
    PRODUCTION("production");

    private final String environment;

    Environment(String environment) {
        this.environment = environment;
    }

    public String value() {
        return environment;
    }
}
