package com.unicovoit.exception;

/**
 * Base exception class for UniCovoit application
 */
public class UniCovoitException extends RuntimeException {

    public UniCovoitException(String message) {
        super(message);
    }

    public UniCovoitException(String message, Throwable cause) {
        super(message, cause);
    }
}
