package com.ratelimiter.strategy;

import com.ratelimiter.model.RateLimiterConfig;
import java.util.HashMap;
import java.util.Map;

public class LeakyBucketStrategy implements RateLimitStrategy {
    private static final double MILLIS_PER_SECOND = 1000.0;
    private static final int EMPTY_QUEUE_SIZE = 0;
    private static final int REQUEST_INCREMENT = 1;

    private final RateLimiterConfig config;
    private final Map<String, Integer> queueSizes;
    private final Map<String, Long> lastLeakTimes;

    public LeakyBucketStrategy(RateLimiterConfig config) {
        this.config = config;
        this.queueSizes = new HashMap<String, Integer>();
        this.lastLeakTimes = new HashMap<String, Long>();
    }

    @Override
    public boolean isAllowed(String clientId) {
        long now = System.currentTimeMillis();
        if (!queueSizes.containsKey(clientId)) {
            queueSizes.put(clientId, EMPTY_QUEUE_SIZE);
            lastLeakTimes.put(clientId, now);
        }

        long elapsed = now - lastLeakTimes.get(clientId);
        double elapsedSeconds = elapsed / MILLIS_PER_SECOND;
        int leaked = (int) (elapsedSeconds * config.getRefillRatePerSecond());

        if (leaked > 0) {
            int updatedQueueSize = queueSizes.get(clientId) - leaked;
            if (updatedQueueSize < EMPTY_QUEUE_SIZE) {
                updatedQueueSize = EMPTY_QUEUE_SIZE;
            }
            queueSizes.put(clientId, updatedQueueSize);
            lastLeakTimes.put(clientId, now);
        }

        int currentQueueSize = queueSizes.get(clientId);
        if (currentQueueSize >= config.getMaxRequests()) {
            return false;
        }

        queueSizes.put(clientId, currentQueueSize + REQUEST_INCREMENT);
        return true;
    }

    @Override
    public RateLimiterConfig getConfig() {
        return config;
    }
}
