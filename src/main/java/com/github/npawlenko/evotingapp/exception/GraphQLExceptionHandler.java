package com.github.npawlenko.evotingapp.exception;

import com.github.npawlenko.evotingapp.utils.MessageUtility;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    private final MessageUtility messageUtility;

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof ApiRequestException apiRequestException) {
            ApiRequestExceptionReason reason = apiRequestException.getReason();
            Map<String, Object> extensions = new HashMap<>();
            extensions.put("errorCode", reason.getHttpStatus().value());

            return GraphqlErrorBuilder.newError(env)
                    .message(messageUtility.getMessage(
                            reason.getMessageKey(),
                            apiRequestException.getArgs()
                    ))
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
