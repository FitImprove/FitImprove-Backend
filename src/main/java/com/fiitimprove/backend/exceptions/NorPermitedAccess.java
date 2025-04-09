package com.fiitimprove.backend.exceptions;

public class NorPermitedAccess extends RuntimeException {
    public NorPermitedAccess(String message) {
        super(message);
    }
}