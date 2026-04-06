package com.distributedcache.observer;

import com.distributedcache.cache.CacheNode;

public class InvalidationObserver implements CacheObserver {
    private final CacheNode targetNode;

    public InvalidationObserver(CacheNode targetNode) {
        this.targetNode = targetNode;
    }

    @Override
    public void onInvalidate(String key) {
        System.out.println("Observer: invalidating key [" + key + "] on node [" + targetNode.getNodeId() + "]");
        targetNode.evict(key);
    }

    @Override
    public void onNodeAdded(String nodeId) {
        System.out.println("Observer: node [" + nodeId + "] added.");
    }

    @Override
    public void onNodeRemoved(String nodeId) {
        System.out.println("Observer: node [" + nodeId + "] removed.");
    }

    public CacheNode getTargetNode() {
        return targetNode;
    }
}
