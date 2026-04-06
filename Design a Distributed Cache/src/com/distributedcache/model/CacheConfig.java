package com.distributedcache.model;

public class CacheConfig {
    private final int maxSizePerNode;
    private final int defaultTtlSeconds;

    public CacheConfig(int maxSizePerNode, int defaultTtlSeconds) {
        this.maxSizePerNode = maxSizePerNode;
        this.defaultTtlSeconds = defaultTtlSeconds;
    }

    public int getMaxSizePerNode() {
        return maxSizePerNode;
    }

    public int getDefaultTtlSeconds() {
        return defaultTtlSeconds;
    }
}
