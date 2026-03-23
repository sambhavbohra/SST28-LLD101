package com.pen.exception;

public class UnknownPenTypeException extends RuntimeException {
    public UnknownPenTypeException(String message) {
        super(message);
    }

    public UnknownPenTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
