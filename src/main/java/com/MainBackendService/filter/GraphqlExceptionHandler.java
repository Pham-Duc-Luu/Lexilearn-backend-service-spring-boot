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
import graphql.execution.ResultPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
public class GraphqlExceptionHandler implements DataFetcherExceptionHandler {
    Logger logger = LogManager.getLogger(GraphqlExceptionHandler.class);

    @Override
    public CompletableFuture<DataFetcherExceptionHandlerResult> handleException(DataFetcherExceptionHandlerParameters handlerParameters) {


        if (handlerParameters.getException() instanceof HttpResponseException httpResponseException) {

            return CompletableFuture.completedFuture(buildErrorFromHttpException(httpResponseException, handlerParameters.getPath()));
        }
        // handle async error
        else if (handlerParameters.getException() instanceof CompletionException) {
            Throwable cause = handlerParameters.getException().getCause();
            if (cause instanceof UndeclaredThrowableException undeclared) {
                Throwable realCause = undeclared.getUndeclaredThrowable();
                if (realCause instanceof HttpResponseException httpResponseException) {
                    return CompletableFuture.completedFuture(buildErrorFromHttpException(httpResponseException, handlerParameters.getPath()));
                }
            }

        }

        // * return by default
        return new DefaultDataFetcherExceptionHandler().handleException(handlerParameters);

    }

    private DataFetcherExceptionHandlerResult buildErrorFromHttpException(HttpResponseException httpResponseException, ResultPath resultPath) {
        HttpErrorDto httpErrorDto = httpResponseException.getErrorDetails();
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
                .path(resultPath).build();

        DataFetcherExceptionHandlerResult result = DataFetcherExceptionHandlerResult.newResult()
                .error(graphqlError)
                .build();

        return result;
    }
}
