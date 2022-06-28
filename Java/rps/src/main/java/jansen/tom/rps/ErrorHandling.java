package jansen.tom.rps;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Arrays;

@ControllerAdvice
public class ErrorHandling {

    public static class Error {

        private final Timestamp timestamp;
        private final Integer status;
        private final String error;
        private final String message;
        private final String path;

        public Error(String path, String message, HttpStatus status) {
            this.timestamp = Timestamp.from(ZonedDateTime.now().toInstant());
            this.status = status.value();
            this.error = status.getReasonPhrase();
            this.message = cleanMessage(message);
            this.path = path;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public Integer getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getPath() {
            return path;
        }

        public String getMessage() {
            return message;
        }

        private String cleanMessage(String input) {
            String[] parts = input.split("\"");
            for (int i = 0; i < parts.length; i++)
                parts[i] = parts[i].trim().replaceAll(" +", " ");
            String[] clean = Arrays.copyOfRange(parts, 1, parts.length);
            return String.join(" ", clean);
        }

    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> errorHandling(HttpServletRequest request, ResponseStatusException error) {
        Error errorMessage = new Error(request.getRequestURI(), error.getMessage(), error.getStatus());
        return new ResponseEntity<Error>(errorMessage,error.getResponseHeaders(),error.getStatus());
    }

}
