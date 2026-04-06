package com.ratelimiter.strategy;

import com.ratelimiter.model.RateLimiterConfig;
import java.util.HashMap;
import java.util.Map;

public class TokenBucketStrategy implements RateLimitStrategy {
    private static final double MILLIS_PER_SECOND = 1000.0;
    private static final double TOKEN_COST_PER_REQUEST = 1.0;

    private final RateLimiterConfig config;
    private final Map<String, Double> tokenBuckets;
    private final Map<String, Long> lastRefillTimes;

    public TokenBucketStrategy(RateLimiterConfig config) {
        this.config = config;
        this.tokenBuckets = new HashMap<String, Double>();
        this.lastRefillTimes = new HashMap<String, Long>();
    }

    @Override
    public boolean isAllowed(String clientId) {
        long now = System.currentTimeMillis();
        if (!tokenBuckets.containsKey(clientId)) {
            tokenBuckets.put(clientId, (double) config.getMaxRequests());
            lastRefillTimes.put(clientId, now);
        }

        long elapsed = now - lastRefillTimes.get(clientId);
        double elapsedSeconds = elapsed / MILLIS_PER_SECOND;
        double tokensToAdd = elapsedSeconds * config.getRefillRatePerSecond();

        double newTokens = tokenBuckets.get(clientId) + tokensToAdd;
        if (newTokens > config.getMaxRequests()) {
            newTokens = config.getMaxRequests();
        }

        tokenBuckets.put(clientId, newTokens);
        lastRefillTimes.put(clientId, now);

        if (newTokens < TOKEN_COST_PER_REQUEST) {
            return false;
        }

        tokenBuckets.put(clientId, newTokens - TOKEN_COST_PER_REQUEST);
        return true;
    }

    @Override
    public RateLimiterConfig getConfig() {
        return config;
    }
}
