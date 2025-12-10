package com.unicovoit.exception;

/**
 * Exception thrown when business logic rules are violated
 */
public class BusinessException extends UniCovoitException {

    public BusinessException(String message) {
        super(message);
    }
}
