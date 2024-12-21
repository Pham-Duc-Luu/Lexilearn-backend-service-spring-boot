package com.MainBackendService.filter;

import com.MainBackendService.controller.Auth.Auth;
import com.MainBackendService.dto.HttpErrorDto;
import jakarta.validation.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionFilter {
    Logger logger = LogManager.getLogger(Auth.class);

    // Handle validation errors for @Valid request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpErrorDto> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        HttpErrorDto errorResponse = new HttpErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errorMessage,
                request.getDescription(false)
        );

        logger.error(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle @NotBlank, @Min, etc. on query params or fields
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HttpErrorDto> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        HttpErrorDto errorResponse = new HttpErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                errorMessage,
                request.getDescription(false)
        );
        logger.error(ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle invalid or missing request body
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<HttpErrorDto> handleInvalidRequestBody(SecurityException ex, WebRequest request) {
        HttpErrorDto errorResponse = new HttpErrorDto(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Invalid request body: " + ex.getMessage(),
                request.getDescription(false)
        );
        logger.error(ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle invalid or missing request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpErrorDto> handleInvalidRequestBody(HttpMessageNotReadableException ex, WebRequest request) {
        HttpErrorDto errorResponse = new HttpErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON Request",
                "Invalid request body: " + ex.getMostSpecificCause().getMessage(),
                request.getDescription(false)
        );
        logger.error(ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorDto> handleGenericException(Exception ex, WebRequest request) {
        HttpErrorDto errorResponse = new HttpErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false)
        );
        ex.printStackTrace();
        logger.error(ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
