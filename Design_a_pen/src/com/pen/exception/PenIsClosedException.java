package com.pen.exception;

public class PenIsClosedException extends RuntimeException {
    public PenIsClosedException(String message) {
        super(message);
    }

    public PenIsClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
