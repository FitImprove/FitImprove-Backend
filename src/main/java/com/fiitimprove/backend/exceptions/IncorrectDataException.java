package com.fiitimprove.backend.exceptions;

public class IncorrectDataException extends RuntimeException {
    public IncorrectDataException(String message) {
        super(message);
    }
}