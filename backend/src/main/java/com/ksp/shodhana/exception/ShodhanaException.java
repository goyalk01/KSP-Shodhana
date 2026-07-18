package com.ksp.shodhana.exception;

/**
 * Base exception for all Shodhana-specific errors.
 */
public class ShodhanaException extends RuntimeException {

    private final String errorCode;

    public ShodhanaException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ShodhanaException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
