package com.fiitimprove.backend.exceptions;

/**
 * Excption related to trying to access resource that does not exists
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}