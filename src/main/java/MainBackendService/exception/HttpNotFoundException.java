package MainBackendService.exception;

import MainBackendService.dto.HttpErrorDto;
import org.springframework.http.HttpStatus;

public class HttpNotFoundException extends HttpResponseException {
    public HttpNotFoundException(String message) {
        super(new HttpErrorDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                message
        ));
    }

    public HttpNotFoundException() {
        super(new HttpErrorDto(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.getReasonPhrase()
        ));
    }
}
