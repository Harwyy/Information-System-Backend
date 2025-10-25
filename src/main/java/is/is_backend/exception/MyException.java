package is.is_backend.exception;

import org.springframework.http.HttpStatus;

public class MyException extends RuntimeException {
    private final HttpStatus httpStatus;

    public MyException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public MyException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
