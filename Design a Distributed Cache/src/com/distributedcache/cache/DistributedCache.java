package com.distributedcache.cache;

import com.distributedcache.db.DatabaseLoader;
import com.distributedcache.exception.NodeNotFoundException;
import com.distributedcache.model.CacheConfig;
import com.distributedcache.observer.CacheObserver;
import com.distributedcache.observer.InvalidationObserver;
import com.distributedcache.strategy.DistributionStrategy;
import java.util.ArrayList;
import java.util.List;

public class DistributedCache implements Cache {
    private final List<CacheNode> nodes;
    private final DistributionStrategy distributionStrategy;
    private final DatabaseLoader databaseLoader;
    private final CacheConfig config;

    public DistributedCache(CacheConfig config, DistributionStrategy strategy, DatabaseLoader loader) {
        this.nodes = new ArrayList<CacheNode>();
        this.distributionStrategy = strategy;
        this.databaseLoader = loader;
        this.config = config;
    }

    public void addNode(CacheNode node) {
        for (CacheNode existingNode : nodes) {
            existingNode.registerObserver(new InvalidationObserver(node));
            node.registerObserver(new InvalidationObserver(existingNode));
        }

        nodes.add(node);
        System.out.println("DistributedCache: node [" + node.getNodeId() + "] added. Total nodes: [" + nodes.size() + "].");

        for (CacheNode currentNode : nodes) {
            List<CacheObserver> nodeObservers = currentNode.getObservers();
            for (CacheObserver observer : nodeObservers) {
                observer.onNodeAdded(node.getNodeId());
            }
        }
    }

    public void removeNode(CacheNode node) {
        boolean removed = nodes.remove(node);
        if (!removed) {
            return;
        }

        System.out.println("DistributedCache: node [" + node.getNodeId() + "] removed.");

        for (CacheNode currentNode : nodes) {
            List<CacheObserver> nodeObservers = currentNode.getObservers();
            for (CacheObserver observer : nodeObservers) {
                observer.onNodeRemoved(node.getNodeId());
            }
        }
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, config.getDefaultTtlSeconds());
    }

    @Override
    public void put(String key, Object value, int ttlSeconds) {
        if (nodes.isEmpty()) {
            throw new NodeNotFoundException("No nodes in cache.");
        }

        CacheNode node = distributionStrategy.getNodeForKey(key, nodes);
        node.put(key, value, ttlSeconds);
    }

    @Override
    public Object get(String key) {
        if (nodes.isEmpty()) {
            throw new NodeNotFoundException("No nodes in cache.");
        }

        CacheNode node = distributionStrategy.getNodeForKey(key, nodes);
        Object result = node.get(key);
        if (result != null) {
            return result;
        }

        System.out.println("DistributedCache: cache miss for key [" + key + "], loading from DB.");
        Object loadedValue = databaseLoader.load(key);
        if (loadedValue != null) {
            node.put(key, loadedValue, config.getDefaultTtlSeconds());
        }

        return loadedValue;
    }

    @Override
    public void evict(String key) {
        for (CacheNode node : nodes) {
            node.evict(key);
        }
    }

    @Override
    public void clear() {
        for (CacheNode node : nodes) {
            node.clear();
        }
    }

    public List<CacheNode> getNodes() {
        return nodes;
    }
}
