package com.MainBackendService.exception;

import com.MainBackendService.dto.HttpErrorDto;
import org.springframework.http.HttpStatus;

public class HttpForbiddenException extends HttpResponseException {
    public HttpForbiddenException(String message) {
        super(new HttpErrorDto(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                message
        ));
    }

    public HttpForbiddenException() {
        super(new HttpErrorDto(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                HttpStatus.FORBIDDEN.getReasonPhrase()
        ));
    }
}