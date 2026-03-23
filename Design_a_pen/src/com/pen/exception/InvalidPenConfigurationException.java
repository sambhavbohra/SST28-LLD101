package com.pen.exception;

public class InvalidPenConfigurationException extends RuntimeException {
    public InvalidPenConfigurationException(String message) {
        super(message);
    }

    public InvalidPenConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
