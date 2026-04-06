package com.ratelimiter.strategy;

import com.ratelimiter.model.RateLimiterConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlidingWindowStrategy implements RateLimitStrategy {
    private static final int MILLIS_PER_SECOND = 1000;

    private final RateLimiterConfig config;
    private final Map<String, List<Long>> requestTimestamps;

    public SlidingWindowStrategy(RateLimiterConfig config) {
        this.config = config;
        this.requestTimestamps = new HashMap<String, List<Long>>();
    }

    @Override
    public boolean isAllowed(String clientId) {
        if (!requestTimestamps.containsKey(clientId)) {
            requestTimestamps.put(clientId, new ArrayList<Long>());
        }

        long now = System.currentTimeMillis();
        long windowMillis = config.getWindowSizeSeconds() * MILLIS_PER_SECOND;
        List<Long> timestamps = requestTimestamps.get(clientId);

        int index = 0;
        while (index < timestamps.size()) {
            long timestamp = timestamps.get(index);
            if (now - timestamp > windowMillis) {
                timestamps.remove(index);
                continue;
            }
            index = index + 1;
        }

        int currentCount = timestamps.size();
        if (currentCount >= config.getMaxRequests()) {
            return false;
        }

        timestamps.add(now);
        return true;
    }

    @Override
    public RateLimiterConfig getConfig() {
        return config;
    }
}
