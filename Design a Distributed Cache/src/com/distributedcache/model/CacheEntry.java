package com.distributedcache.model;

public class CacheEntry {
    private final String key;
    private final Object value;
    private final long expiresAtMillis;
    private long lastAccessedMillis;

    public CacheEntry(String key, Object value, int ttlSeconds) {
        long currentTimeMillis = System.currentTimeMillis();
        this.key = key;
        this.value = value;
        this.expiresAtMillis = currentTimeMillis + (ttlSeconds * 1000L);
        this.lastAccessedMillis = currentTimeMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAtMillis;
    }

    public void updateLastAccessed() {
        lastAccessedMillis = System.currentTimeMillis();
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public long getExpiresAtMillis() {
        return expiresAtMillis;
    }

    public long getLastAccessedMillis() {
        return lastAccessedMillis;
    }
}
