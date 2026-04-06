package com.distributedcache.strategy;

import com.distributedcache.model.CacheEntry;
import java.util.Map;

public class LRUEvictionPolicy implements EvictionPolicy {
    @Override
    public void onAccess(String key) {
    }

    @Override
    public void onInsert(String key) {
    }

    @Override
    public String pickEvictionCandidate(Map<String, CacheEntry> store) {
        String oldestKey = null;
        long oldestAccessTime = Long.MAX_VALUE;

        for (Map.Entry<String, CacheEntry> currentEntry : store.entrySet()) {
            CacheEntry cacheEntry = currentEntry.getValue();
            long lastAccessedMillis = cacheEntry.getLastAccessedMillis();
            if (lastAccessedMillis < oldestAccessTime) {
                oldestAccessTime = lastAccessedMillis;
                oldestKey = currentEntry.getKey();
            }
        }

        return oldestKey;
    }
}
