package com.fiitimprove.backend.exceptions;

/**
 * Exception related to user not having rights to access certain information
 */
public class NorPermitedAccess extends RuntimeException {
    public NorPermitedAccess(String message) {
        super(message);
    }
}