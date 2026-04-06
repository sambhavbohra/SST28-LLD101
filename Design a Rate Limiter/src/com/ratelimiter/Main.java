package com.ratelimiter;

import com.ratelimiter.api.RemoteAPIService;
import com.ratelimiter.exception.RateLimitExceededException;
import com.ratelimiter.model.RateLimiterConfig;
import com.ratelimiter.model.Response;
import com.ratelimiter.proxy.RateLimitedAPIProxy;
import com.ratelimiter.real.RealRemoteAPIService;
import com.ratelimiter.strategy.FixedWindowStrategy;
import com.ratelimiter.strategy.LeakyBucketStrategy;
import com.ratelimiter.strategy.RateLimitStrategy;
import com.ratelimiter.strategy.SlidingWindowStrategy;
import com.ratelimiter.strategy.TokenBucketStrategy;

public class Main {
    private static final int MAX_REQUESTS = 3;
    private static final int WINDOW_SIZE_SECONDS = 10;
    private static final int REFILL_RATE_PER_SECOND = 1;
    private static final int TOTAL_CALLS_PER_SECTION = 5;

    public static void main(String[] args) {
        System.out.println("=== FixedWindowStrategy ===");
        RateLimiterConfig fixedWindowConfig = new RateLimiterConfig(MAX_REQUESTS, WINDOW_SIZE_SECONDS, REFILL_RATE_PER_SECOND);
        RateLimitStrategy fixedWindowStrategy = new FixedWindowStrategy(fixedWindowConfig);
        RemoteAPIService fixedWindowRealService = new RealRemoteAPIService("https://api.example.com");
        RemoteAPIService fixedWindowProxy = new RateLimitedAPIProxy(fixedWindowRealService, fixedWindowStrategy);
        for (int callIndex = 1; callIndex <= TOTAL_CALLS_PER_SECTION; callIndex = callIndex + 1) {
            try {
                Response response = fixedWindowProxy.call("client-A", "request-" + callIndex);
                System.out.println("Call " + callIndex + " succeeded: " + response.getBody());
            } catch (RateLimitExceededException exception) {
                System.out.println("Call " + callIndex + " blocked: " + exception.getMessage());
            }
        }

        System.out.println("=== SlidingWindowStrategy ===");
        RateLimiterConfig slidingWindowConfig = new RateLimiterConfig(MAX_REQUESTS, WINDOW_SIZE_SECONDS, REFILL_RATE_PER_SECOND);
        RateLimitStrategy slidingWindowStrategy = new SlidingWindowStrategy(slidingWindowConfig);
        RemoteAPIService slidingWindowRealService = new RealRemoteAPIService("https://api.example.com");
        RemoteAPIService slidingWindowProxy = new RateLimitedAPIProxy(slidingWindowRealService, slidingWindowStrategy);
        for (int callIndex = 1; callIndex <= TOTAL_CALLS_PER_SECTION; callIndex = callIndex + 1) {
            try {
                Response response = slidingWindowProxy.call("client-A", "request-" + callIndex);
                System.out.println("Call " + callIndex + " succeeded: " + response.getBody());
            } catch (RateLimitExceededException exception) {
                System.out.println("Call " + callIndex + " blocked: " + exception.getMessage());
            }
        }

        System.out.println("=== TokenBucketStrategy ===");
        RateLimiterConfig tokenBucketConfig = new RateLimiterConfig(MAX_REQUESTS, WINDOW_SIZE_SECONDS, REFILL_RATE_PER_SECOND);
        RateLimitStrategy tokenBucketStrategy = new TokenBucketStrategy(tokenBucketConfig);
        RemoteAPIService tokenBucketRealService = new RealRemoteAPIService("https://api.example.com");
        RemoteAPIService tokenBucketProxy = new RateLimitedAPIProxy(tokenBucketRealService, tokenBucketStrategy);
        for (int callIndex = 1; callIndex <= TOTAL_CALLS_PER_SECTION; callIndex = callIndex + 1) {
            try {
                Response response = tokenBucketProxy.call("client-A", "request-" + callIndex);
                System.out.println("Call " + callIndex + " succeeded: " + response.getBody());
            } catch (RateLimitExceededException exception) {
                System.out.println("Call " + callIndex + " blocked: " + exception.getMessage());
            }
        }

        System.out.println("=== LeakyBucketStrategy ===");
        RateLimiterConfig leakyBucketConfig = new RateLimiterConfig(MAX_REQUESTS, WINDOW_SIZE_SECONDS, REFILL_RATE_PER_SECOND);
        RateLimitStrategy leakyBucketStrategy = new LeakyBucketStrategy(leakyBucketConfig);
        RemoteAPIService leakyBucketRealService = new RealRemoteAPIService("https://api.example.com");
        RemoteAPIService leakyBucketProxy = new RateLimitedAPIProxy(leakyBucketRealService, leakyBucketStrategy);
        for (int callIndex = 1; callIndex <= TOTAL_CALLS_PER_SECTION; callIndex = callIndex + 1) {
            try {
                Response response = leakyBucketProxy.call("client-A", "request-" + callIndex);
                System.out.println("Call " + callIndex + " succeeded: " + response.getBody());
            } catch (RateLimitExceededException exception) {
                System.out.println("Call " + callIndex + " blocked: " + exception.getMessage());
            }
        }

        System.out.println("=== Two clients, FixedWindow ===");
        RateLimiterConfig twoClientConfig = new RateLimiterConfig(2, WINDOW_SIZE_SECONDS, REFILL_RATE_PER_SECOND);
        RateLimitStrategy twoClientStrategy = new FixedWindowStrategy(twoClientConfig);
        RemoteAPIService twoClientRealService = new RealRemoteAPIService("https://api.example.com");
        RemoteAPIService twoClientProxy = new RateLimitedAPIProxy(twoClientRealService, twoClientStrategy);

        String[] clients = new String[] {"client-A", "client-B", "client-A", "client-B", "client-A", "client-B"};
        for (int callIndex = 0; callIndex < clients.length; callIndex = callIndex + 1) {
            String clientId = clients[callIndex];
            int callNumber = callIndex + 1;
            try {
                Response response = twoClientProxy.call(clientId, "request-" + callNumber);
                System.out.println("Call " + callNumber + " for " + clientId + " succeeded: " + response.getBody());
            } catch (RateLimitExceededException exception) {
                System.out.println("Call " + callNumber + " for " + clientId + " blocked: " + exception.getMessage());
            }
        }
    }
}
