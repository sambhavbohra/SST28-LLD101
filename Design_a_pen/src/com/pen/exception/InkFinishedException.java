package com.pen.exception;

public class InkFinishedException extends RuntimeException {
    public InkFinishedException(String message) {
        super(message);
    }

    public InkFinishedException(String message, Throwable cause) {
        super(message, cause);
    }
}
