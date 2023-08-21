package com.github.npawlenko.evotingapp.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ApiRequestException apiRequestException) {
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", apiRequestException.getHttpStatus().value());

            return GraphqlErrorBuilder.newError(env)
                    .message(apiRequestException.getMessage())
                    .errorType(ErrorType.DataFetchingException)
                    .extensions(extensions)
                    .build();
        }

        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.DataFetchingException)
                .build();
    }
}
