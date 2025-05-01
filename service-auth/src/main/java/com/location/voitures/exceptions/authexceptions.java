package com.location.voitures.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class authexceptions {

    // 400 Bad Request
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException() {
            super("Invalid email format");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class WeakPasswordException extends RuntimeException {
        public WeakPasswordException() {
            super("Password must be at least 8 characters");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidNameException extends RuntimeException {
        public InvalidNameException() {
            super("Name is required and cannot be empty");
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidAgeException extends RuntimeException {
        public InvalidAgeException() {
            super("User must be at least 18 years old");
        }
    }
    
    
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidAddressException extends RuntimeException {
        public InvalidAddressException() {
            super("Address cannot be empty");
        }

        public InvalidAddressException(String message) {
            super(message);
        }
    }


    // 401 Unauthorized
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException() {
            super("Account not found");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class IncorrectPasswordException extends RuntimeException {
        public IncorrectPasswordException() {
            super("Incorrect password");
        }
    }

    // 409 Conflict
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EmailExistsException extends RuntimeException {
        public EmailExistsException() {
            super("Email already registered");
        }
    }
    
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
    
}