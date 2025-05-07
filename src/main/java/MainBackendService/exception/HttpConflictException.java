package MainBackendService.exception;

import MainBackendService.dto.HttpErrorDto;
import org.springframework.http.HttpStatus;

public class HttpConflictException extends HttpResponseException {
    public HttpConflictException(String message) {
        super(new HttpErrorDto(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                message
        ));
    }

    public HttpConflictException() {
        super(new HttpErrorDto(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                HttpStatus.CONFLICT.getReasonPhrase()
        ));
    }
}
