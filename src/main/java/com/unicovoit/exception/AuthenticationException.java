package com.unicovoit.exception;

/**
 * Exception thrown when authentication fails
 */
public class AuthenticationException extends UniCovoitException {

    public AuthenticationException(String message) {
        super(message);
    }
}
