package com.smoothy.authentication.infrastructure.config.handler;

import com.smoothy.authentication.infrastructure.Exceptions.ConflictException;
import com.smoothy.authentication.infrastructure.Exceptions.ForbiddenException;
import com.smoothy.authentication.infrastructure.Exceptions.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> handleConflictException(ConflictException ex, HttpServletRequest request) {
        String message = ex.getMessage();

        return buildResponse(HttpStatus.CONFLICT, message, request);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorMessage> handleForbidden(ConflictException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return buildResponse(HttpStatus.FORBIDDEN, message, request);
    }

//    @ExceptionHandler(ValidationException.class)
//    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException ex, HttpServletRequest request) {
//        String message = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                .findFirst()
//                .orElse("Validation failed");
//
//        return buildResponse(HttpStatus.UNAUTHORIZED , message, request);
//    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(ValidationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorMessage> handleGenericException(Exception ex, HttpServletRequest request) {
//        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request);
//    }

    private ResponseEntity<ErrorMessage> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorMessage error = new ErrorMessage(LocalDateTime.now().format(formatter), status.value(), message, request.getRequestURI());
        return new ResponseEntity<>(error, status);
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorMessage {
        private final String timestamp;
        private final int status;
        private final String message;
        private final String path;
    }
}
