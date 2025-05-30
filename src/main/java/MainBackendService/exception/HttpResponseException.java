package MainBackendService.exception;

import MainBackendService.dto.HttpErrorDto;

public class HttpResponseException extends Exception {
    private final HttpErrorDto errorDetails;

    public HttpResponseException(HttpErrorDto errorDetails) {
        super(errorDetails.getMessage());
        this.errorDetails = errorDetails;
    }

    public HttpErrorDto getErrorDetails() {
        return errorDetails;
    }
}
