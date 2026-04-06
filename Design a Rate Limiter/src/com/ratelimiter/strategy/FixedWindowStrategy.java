package com.ratelimiter.strategy;

import com.ratelimiter.model.RateLimiterConfig;
import java.util.HashMap;
import java.util.Map;

public class FixedWindowStrategy implements RateLimitStrategy {
    private static final int MILLIS_PER_SECOND = 1000;
    private static final int INITIAL_REQUEST_COUNT = 0;
    private static final int REQUEST_INCREMENT = 1;

    private final RateLimiterConfig config;
    private final Map<String, Integer> requestCounts;
    private final Map<String, Long> windowStartTimes;

    public FixedWindowStrategy(RateLimiterConfig config) {
        this.config = config;
        this.requestCounts = new HashMap<String, Integer>();
        this.windowStartTimes = new HashMap<String, Long>();
    }

    @Override
    public boolean isAllowed(String clientId) {
        long now = System.currentTimeMillis();
        if (!windowStartTimes.containsKey(clientId)) {
            windowStartTimes.put(clientId, now);
            requestCounts.put(clientId, INITIAL_REQUEST_COUNT);
        }

        long elapsed = now - windowStartTimes.get(clientId);
        long windowMillis = config.getWindowSizeSeconds() * MILLIS_PER_SECOND;
        if (elapsed >= windowMillis) {
            windowStartTimes.put(clientId, now);
            requestCounts.put(clientId, INITIAL_REQUEST_COUNT);
        }

        int currentCount = requestCounts.get(clientId);
        if (currentCount >= config.getMaxRequests()) {
            return false;
        }

        requestCounts.put(clientId, currentCount + REQUEST_INCREMENT);
        return true;
    }

    @Override
    public RateLimiterConfig getConfig() {
        return config;
    }
}
