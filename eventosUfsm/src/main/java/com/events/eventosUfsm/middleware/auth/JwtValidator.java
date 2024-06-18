package com.events.eventosUfsm.middleware.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class JwtValidator {

    @ExceptionHandler(InvalidToken.class)
    public ResponseEntity<?> invalidToken(InvalidToken value) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(value.getMessage());
    }

    @ExceptionHandler(ExpiredToken.class)
    public ResponseEntity<?> expiredToken(InvalidToken value) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(value.getMessage());
    }

    public static class InvalidToken extends RuntimeException {
        public InvalidToken(String message) {
            super(message);
        }
    }

    public static class ExpiredToken extends RuntimeException {
        public ExpiredToken(String message) {
            super(message);
        }
    }
}
