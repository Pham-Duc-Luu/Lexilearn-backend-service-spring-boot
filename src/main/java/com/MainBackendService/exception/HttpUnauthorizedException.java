package com.MainBackendService.exception;

import com.MainBackendService.dto.HttpErrorDto;
import org.springframework.http.HttpStatus;

public class HttpUnauthorizedException extends HttpResponseException {
    public HttpUnauthorizedException(String message) {
        super(new HttpErrorDto(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                message
        ));
    }

    public HttpUnauthorizedException() {
        super(new HttpErrorDto(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase()
        ));
    }
}
