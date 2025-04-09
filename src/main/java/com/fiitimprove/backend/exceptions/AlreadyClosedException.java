package com.fiitimprove.backend.exceptions;

public class AlreadyClosedException extends RuntimeException {
    public AlreadyClosedException(String message) {
        super(message);
    }
}