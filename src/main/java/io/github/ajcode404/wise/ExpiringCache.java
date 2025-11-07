package io.github.ajcode404.wise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExpiringCache<K, V> {
    private static class CacheEntry<V> {
        final V value;
        final long expiryTime;
        CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expiryTime = ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final ConcurrentHashMap<K, CacheEntry<V>> map = new ConcurrentHashMap<>();
    private final int maxSize;
    private final AtomicInteger size = new AtomicInteger(0);
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    public ExpiringCache(int maxSize, long cleanupIntervalsMillis) {
        this.maxSize = maxSize;

        cleaner.scheduleAtFixedRate(this::cleanup, cleanupIntervalsMillis, cleanupIntervalsMillis, TimeUnit.MILLISECONDS);
    }

    public void put(K key, V value, long ttlMillis) {
        if (size.get() >= maxSize) {
            evictOldest();
        }
        CacheEntry<V> entry = new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis);
        map.put(key, entry);
        size.set(map.size());
    }

    public V get(K key) {
        CacheEntry<V> entry = map.get(key);
        if (entry == null) return null;
        if (entry.isExpired()) {
            map.remove(key);
            size.decrementAndGet();
            return null;
        }
        return entry.value;
    }

    private void evictOldest() {
        long oldestExpiry = Long.MAX_VALUE;
        K oldestKey = null;
        for (Map.Entry<K, CacheEntry<V>> e: map.entrySet()) {
            if (e.getValue().expiryTime < oldestExpiry) {
                oldestExpiry = e.getValue().expiryTime;
                oldestKey = e.getKey();
            }
        }
        if (oldestKey != null) {
            map.remove(oldestKey);
            size.decrementAndGet();
        }
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        for (Map.Entry<K, CacheEntry<V>> e : map.entrySet()) {
            if (e.getValue().expiryTime <= now) {
                map.remove(e.getKey());
                size.decrementAndGet();
            }
        }
    }

    public void shutdown() {
        cleaner.shutdown();
    }

    public static void main(String[] args) throws Exception {
        ExpiringCache<String, String> cache = new ExpiringCache<>(5, 2000);

        cache.put("user1", "Akash", 3000);
        System.out.println("user1 -> " + cache.get("user1")); // Akash

        Thread.sleep(4000);
        System.out.println("user1 -> " + cache.get("user1")); // null (expired)

        cache.shutdown();
    }
}

