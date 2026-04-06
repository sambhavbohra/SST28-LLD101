package com.distributedcache.cache;

import com.distributedcache.model.CacheConfig;
import com.distributedcache.model.CacheEntry;
import com.distributedcache.observer.CacheObserver;
import com.distributedcache.observer.InvalidationObserver;
import com.distributedcache.strategy.EvictionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheNode implements Cache {
    private final String nodeId;
    private final Map<String, CacheEntry> store;
    private final EvictionPolicy evictionPolicy;
    private final int maxSize;
    private final int defaultTtlSeconds;
    private final List<CacheObserver> observers;

    public CacheNode(String nodeId, CacheConfig config, EvictionPolicy policy) {
        this.nodeId = nodeId;
        this.store = new HashMap<String, CacheEntry>();
        this.evictionPolicy = policy;
        this.maxSize = config.getMaxSizePerNode();
        this.defaultTtlSeconds = config.getDefaultTtlSeconds();
        this.observers = new ArrayList<CacheObserver>();
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, defaultTtlSeconds);
    }

    @Override
    public void put(String key, Object value, int ttlSeconds) {
        boolean keyAlreadyExists = store.containsKey(key);
        if (store.size() >= maxSize && !keyAlreadyExists) {
            String victimKey = evictionPolicy.pickEvictionCandidate(store);
            if (victimKey != null) {
                store.remove(victimKey);
                System.out.println("Node [" + nodeId + "]: evicted key [" + victimKey + "] to make room.");
            }
        }

        CacheEntry cacheEntry = new CacheEntry(key, value, ttlSeconds);
        evictionPolicy.onInsert(key);
        store.put(key, cacheEntry);
        System.out.println("Node [" + nodeId + "]: stored key [" + key + "].");

        for (CacheObserver observer : observers) {
            if (observer instanceof InvalidationObserver) {
                InvalidationObserver invalidationObserver = (InvalidationObserver) observer;
                if (invalidationObserver.getTargetNode().getNodeId().equals(nodeId)) {
                    continue;
                }
            }
            observer.onInvalidate(key);
        }
    }

    @Override
    public Object get(String key) {
        if (!store.containsKey(key)) {
            return null;
        }

        CacheEntry entry = store.get(key);
        if (entry.isExpired()) {
            store.remove(key);
            System.out.println("Node [" + nodeId + "]: key [" + key + "] expired, removed.");
            return null;
        }

        entry.updateLastAccessed();
        evictionPolicy.onAccess(key);
        return entry.getValue();
    }

    @Override
    public void evict(String key) {
        if (store.containsKey(key)) {
            store.remove(key);
        }
    }

    @Override
    public void clear() {
        store.clear();
    }

    public void registerObserver(CacheObserver observer) {
        observers.add(observer);
    }

    public String getNodeId() {
        return nodeId;
    }

    public List<CacheObserver> getObservers() {
        return observers;
    }
}
