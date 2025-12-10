package com.unicovoit.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends UniCovoitException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s avec l'ID %d n'a pas été trouvé.", resourceName, id));
    }
}
