package com.fiitimprove.backend.exceptions;

/**
 * Exception related to user passing incorrect data
 */
public class IncorrectDataException extends RuntimeException {
    public IncorrectDataException(String message) {
        super(message);
    }
}