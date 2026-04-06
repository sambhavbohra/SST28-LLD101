# Design a Rate Limiter

This project implements a rate-limited API access layer using Proxy and Strategy patterns in Java.

## Features

- Proxy-protected API access through `RateLimitedAPIProxy`
- Pluggable strategies:
  - `FixedWindowStrategy`
  - `SlidingWindowStrategy`
  - `TokenBucketStrategy`
  - `LeakyBucketStrategy`
- Per-client tracking
- Custom exception for blocked calls

## Package Structure

```text
com.ratelimiter
├── Main.java
├── api
│   └── RemoteAPIService.java
├── proxy
│   └── RateLimitedAPIProxy.java
├── real
│   └── RealRemoteAPIService.java
├── strategy
│   ├── RateLimitStrategy.java
│   ├── FixedWindowStrategy.java
│   ├── SlidingWindowStrategy.java
│   ├── TokenBucketStrategy.java
│   └── LeakyBucketStrategy.java
├── model
│   ├── RateLimiterConfig.java
│   └── Response.java
└── exception
    └── RateLimitExceededException.java
```

## Class Diagram

![Class Diagram](class_diagram.png)

```mermaid
classDiagram
    direction LR

    class RemoteAPIService {
      <<interface>>
      +call(clientId, request) Response
    }

    class RealRemoteAPIService {
      -endpoint : String
      +call(clientId, request) Response
    }

    class RateLimitedAPIProxy {
      -realService : RemoteAPIService
      -strategy : RateLimitStrategy
      +call(clientId, request) Response
    }

    class RateLimitStrategy {
      <<interface>>
      +isAllowed(clientId) boolean
      +getConfig() RateLimiterConfig
    }

    class FixedWindowStrategy {
      -requestCounts : Map
      -windowStartTimes : Map
      +isAllowed(clientId) boolean
    }

    class SlidingWindowStrategy {
      -requestTimestamps : Map
      +isAllowed(clientId) boolean
    }

    class TokenBucketStrategy {
      -tokenBuckets : Map
      -lastRefillTimes : Map
      +isAllowed(clientId) boolean
    }

    class LeakyBucketStrategy {
      -queueSizes : Map
      -lastLeakTimes : Map
      +isAllowed(clientId) boolean
    }

    class RateLimiterConfig {
      -maxRequests : int
      -windowSizeSeconds : int
      -refillRatePerSecond : int
    }

    class Response {
      -clientId : String
      -body : String
      -statusCode : int
    }

    class RateLimitExceededException

    RemoteAPIService <|.. RealRemoteAPIService
    RemoteAPIService <|.. RateLimitedAPIProxy

    RateLimitStrategy <|.. FixedWindowStrategy
    RateLimitStrategy <|.. SlidingWindowStrategy
    RateLimitStrategy <|.. TokenBucketStrategy
    RateLimitStrategy <|.. LeakyBucketStrategy

    RateLimitedAPIProxy --> RemoteAPIService : delegates
    RateLimitedAPIProxy --> RateLimitStrategy : checks
    RateLimitedAPIProxy ..> RateLimitExceededException : throws

    RealRemoteAPIService --> Response : returns

    FixedWindowStrategy --> RateLimiterConfig
    SlidingWindowStrategy --> RateLimiterConfig
    TokenBucketStrategy --> RateLimiterConfig
    LeakyBucketStrategy --> RateLimiterConfig
```

## Build and Run

From the `Design a Rate Limiter` folder:

```bash
javac -d out $(find src -name '*.java')
java -cp out com.ratelimiter.Main
```
