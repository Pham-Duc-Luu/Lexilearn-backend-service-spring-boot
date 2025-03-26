package com.MainBackendService.exception;


import com.MainBackendService.dto.HttpErrorDto;
import org.springframework.http.HttpStatus;

public class HttpBadRequestException extends HttpResponseException {
    public HttpBadRequestException(String message) {
        super(new HttpErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message
        ));
    }

    public HttpBadRequestException() {
        super(new HttpErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        ));
    }
}