package com.distributedcache.exception;

public class NodeNotFoundException extends CacheException {
    public NodeNotFoundException(String message) {
        super(message);
    }

    public NodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
