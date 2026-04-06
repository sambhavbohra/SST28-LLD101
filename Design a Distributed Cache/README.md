# Design a Distributed Cache

This project implements a distributed cache using Proxy, Strategy, and Observer patterns in Java.

## Features

- Cache access through a proxy-style distributed facade
- Pluggable node distribution strategy
- Pluggable eviction policy (LRU)
- Observer-based invalidation across nodes
- TTL support with lazy expiry
- DB fallback on cache miss

## Package Structure

```text
com.distributedcache
├── Main.java
├── cache
│   ├── Cache.java
│   ├── CacheNode.java
│   └── DistributedCache.java
├── db
│   └── DatabaseLoader.java
├── exception
│   ├── CacheException.java
│   └── NodeNotFoundException.java
├── model
│   ├── CacheConfig.java
│   └── CacheEntry.java
├── observer
│   ├── CacheObserver.java
│   └── InvalidationObserver.java
└── strategy
    ├── DistributionStrategy.java
    ├── ModuloDistributionStrategy.java
    ├── EvictionPolicy.java
    └── LRUEvictionPolicy.java
```

## Class Diagram

![Class Diagram](Class_diagram.png)

```mermaid
classDiagram
  direction LR

  class Cache {
    <<interface>>
    +put(key, value)
    +put(key, value, ttlSeconds)
    +get(key) Object
    +evict(key)
    +clear()
  }

  class CacheNode {
    -nodeId: String
    -store: Map
    -evictionPolicy: EvictionPolicy
    -maxSize: int
    -defaultTtlSeconds: int
    -observers: List
  }

  class DistributedCache {
    -nodes: List
    -distributionStrategy: DistributionStrategy
    -databaseLoader: DatabaseLoader
    -config: CacheConfig
    +addNode(node)
    +removeNode(node)
  }

  class CacheEntry {
    -key: String
    -value: Object
    -expiresAtMillis: long
    -lastAccessedMillis: long
    +isExpired() boolean
    +updateLastAccessed()
  }

  class CacheConfig {
    -maxSizePerNode: int
    -defaultTtlSeconds: int
  }

  class DistributionStrategy {
    <<interface>>
    +getNodeForKey(key, nodes) CacheNode
  }

  class ModuloDistributionStrategy

  class EvictionPolicy {
    <<interface>>
    +onAccess(key)
    +onInsert(key)
    +pickEvictionCandidate(store) String
  }

  class LRUEvictionPolicy

  class CacheObserver {
    <<interface>>
    +onInvalidate(key)
    +onNodeAdded(nodeId)
    +onNodeRemoved(nodeId)
  }

  class InvalidationObserver {
    -targetNode: CacheNode
  }

  class DatabaseLoader {
    -fakeDatabase: Map
    +load(key) Object
  }

  class CacheException
  class NodeNotFoundException

  Cache <|.. CacheNode
  Cache <|.. DistributedCache

  DistributionStrategy <|.. ModuloDistributionStrategy
  EvictionPolicy <|.. LRUEvictionPolicy
  CacheObserver <|.. InvalidationObserver

  CacheNode --> CacheEntry
  CacheNode --> EvictionPolicy
  CacheNode --> CacheObserver

  DistributedCache --> CacheNode
  DistributedCache --> DistributionStrategy
  DistributedCache --> DatabaseLoader
  DistributedCache --> CacheConfig

  InvalidationObserver --> CacheNode

  NodeNotFoundException --|> CacheException
```

## Build and Run

From the Design a Distributed Cache folder:

```bash
javac -d out $(find src -name '*.java')
java -cp out com.distributedcache.Main
```
