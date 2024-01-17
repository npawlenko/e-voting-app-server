package com.github.npawlenko.evotingapp;

import com.github.npawlenko.evotingapp.annotation.TestDatabase;
import com.github.npawlenko.evotingapp.configuration.EmailConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {EmailConfiguration.class})
@TestDatabase
class EVotingAppServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
