package com.distributedcache;

import com.distributedcache.cache.Cache;
import com.distributedcache.cache.CacheNode;
import com.distributedcache.cache.DistributedCache;
import com.distributedcache.db.DatabaseLoader;
import com.distributedcache.model.CacheConfig;
import com.distributedcache.strategy.DistributionStrategy;
import com.distributedcache.strategy.EvictionPolicy;
import com.distributedcache.strategy.LRUEvictionPolicy;
import com.distributedcache.strategy.ModuloDistributionStrategy;

public class Main {
    public static void main(String[] args) throws Exception {
        CacheConfig config = new CacheConfig(3, 60);
        DatabaseLoader db = new DatabaseLoader();
        DistributionStrategy strategy = new ModuloDistributionStrategy();
        Cache cache = new DistributedCache(config, strategy, db);

        EvictionPolicy lru = new LRUEvictionPolicy();
        CacheNode node1 = new CacheNode("node-1", config, lru);
        CacheNode node2 = new CacheNode("node-2", config, lru);
        CacheNode node3 = new CacheNode("node-3", config, lru);

        ((DistributedCache) cache).addNode(node1);
        ((DistributedCache) cache).addNode(node2);
        ((DistributedCache) cache).addNode(node3);

        System.out.println("--- Step 1 - basic put and get ---");
        cache.put("user:1", "Alice");
        cache.put("user:2", "Bob");
        System.out.println("user:1 -> " + cache.get("user:1"));
        System.out.println("user:2 -> " + cache.get("user:2"));

        System.out.println("--- Step 2 - cache miss triggers DB load ---");
        System.out.println("user:3 first read -> " + cache.get("user:3"));
        System.out.println("user:3 second read -> " + cache.get("user:3"));

        System.out.println("--- Step 3 - eviction (LRU) ---");
        node1.clear();
        node1.put("a", "value-a");
        node1.put("b", "value-b");
        node1.put("c", "value-c");
        node1.get("a");
        node1.put("d", "value-d");
        System.out.println("node-1 get b after eviction -> " + node1.get("b"));
        System.out.println("node-1 get a after eviction -> " + node1.get("a"));

        System.out.println("--- Step 4 - observer invalidation ---");
        cache.put("product:1", "Laptop");
        CacheNode routedNode = strategy.getNodeForKey("product:1", ((DistributedCache) cache).getNodes());
        CacheNode staleNode = node2;
        if (routedNode.getNodeId().equals(node2.getNodeId())) {
            staleNode = node3;
        }
        staleNode.put("product:1", "OldLaptop");
        cache.put("product:1", "NewLaptop");
        System.out.println("stale node value after invalidation -> " + staleNode.get("product:1"));
        System.out.println("distributed get product:1 -> " + cache.get("product:1"));

        System.out.println("--- Step 5 - TTL expiry ---");
        cache.put("session:abc", "token123", 1);
        Thread.sleep(2000);
        System.out.println("session:abc -> " + cache.get("session:abc"));

        System.out.println("--- Step 6 - node added mid operation ---");
        ((DistributedCache) cache).addNode(new CacheNode("node-4", config, lru));
        cache.put("user:4", "Dora");
        System.out.println("user:4 -> " + cache.get("user:4"));
    }
}
