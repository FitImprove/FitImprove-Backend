package com.fiitimprove.backend.exceptions;

/**
 * Exception related to user training to enroll in the event that has already finished
 */
public class AlreadyClosedException extends RuntimeException {
    public AlreadyClosedException(String message) {
        super(message);
    }
}