package com.MainBackendService.filter;

import com.MainBackendService.dto.HttpErrorDto;
import com.MainBackendService.exception.HttpResponseException;
import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler;
import com.netflix.graphql.types.errors.ErrorType;
import com.netflix.graphql.types.errors.TypedGraphQLError;
import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class GraphqlExceptionHandler implements DataFetcherExceptionHandler {

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {
        if (handlerParameters.getException() instanceof HttpResponseException) {
            HttpErrorDto httpErrorDto = ((HttpResponseException) handlerParameters.getException()).getErrorDetails();
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("errorDetails", httpErrorDto);

            ErrorType errorType;
            switch (httpErrorDto.getStatus()) {
                case 401:
                    errorType = ErrorType.UNAUTHENTICATED;
                    break;
                case 500:
                    errorType = ErrorType.INTERNAL;
                    break;
                case 404:
                    errorType = ErrorType.NOT_FOUND;
                    break;
                case 403:
                    errorType = ErrorType.PERMISSION_DENIED;
                    break;
                default:
                    errorType = ErrorType.BAD_REQUEST;
            }

            GraphQLError graphqlError = TypedGraphQLError.newInternalErrorBuilder()
                    .message(httpErrorDto.getMessage())
                    .errorType(errorType)
                    .debugInfo(debugInfo)
                    .path(handlerParameters.getPath()).build();

            DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                    .error(graphqlError)
                    .build();

            return CompletableFuture.completedFuture(result);
        } else {
            return new DefaultDataFetcherExceptionHandler().handleException(handlerParameters);
        }
    }
}
