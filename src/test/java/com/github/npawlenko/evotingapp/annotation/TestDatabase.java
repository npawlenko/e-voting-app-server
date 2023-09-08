package com.github.npawlenko.evotingapp.annotation;

import com.github.npawlenko.evotingapp.TestContainersConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
public @interface TestDatabase {
}
