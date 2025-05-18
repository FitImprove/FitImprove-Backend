package com.fiitimprove.backend.exceptions;

/**
 * Exception related to user not having rights to access certain information
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}