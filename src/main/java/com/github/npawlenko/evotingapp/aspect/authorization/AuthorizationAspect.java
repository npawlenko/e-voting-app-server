package com.github.npawlenko.evotingapp.aspect.authorization;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Component
@EnableAspectJAutoProxy
public class AuthorizationAspect {

    @Pointcut("@annotation(org.springframework.graphql.data.method.annotation.QueryMapping)")
    public void queryMapping() {}

    @Pointcut("@annotation(org.springframework.graphql.data.method.annotation.MutationMapping)")
    public void mutationMapping() {}

    @Pointcut("queryMapping() || mutationMapping()")
    public void graphQlMapping() {}

    @Pointcut("@annotation(com.github.npawlenko.evotingapp.aspect.authorization.PublicEndpoint)")
    public void publicEndpoint() {}


    @Around("graphQlMapping() && !publicEndpoint()")
    public Object aroundNotPublicEndpoint(ProceedingJoinPoint joinPoint) {
        //TODO: check authorization header

        throw new RuntimeException();
    }
}
