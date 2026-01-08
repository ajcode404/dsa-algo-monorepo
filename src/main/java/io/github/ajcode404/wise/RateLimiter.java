package io.github.ajcode404.wise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

class FixedWindowRateLimiter {

    private final int maxRequest;
    private final long windowMillis;

    private static class Counter {
        private int count;
        private long windowStart;
        public Counter(int count, long windowStart) {
            this.count = count;
            this.windowStart = windowStart;
        }

        public Counter inc() {
            return new Counter(count + 1, windowStart);
        }

        public Counter reset() {
            return new Counter(0, System.currentTimeMillis());
        }
    }

    private final Map<String, AtomicReference<Counter>> clients = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequest, long windowsMillis) {
        this.maxRequest = maxRequest;
        this.windowMillis = windowsMillis;
    }

    public boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        AtomicReference<Counter> ref = clients.computeIfAbsent(clientId, id -> new AtomicReference<>(new Counter(0, now)));

        while (true) {
            Counter curr = ref.get();
            Counter updated;
            if (now - curr.windowStart >= windowMillis) {
                updated = new Counter(1, now);
            } else if (curr.count < maxRequest) {
                updated = new Counter(curr.count + 1, curr.windowStart);
            } else {
                return false;
            }

            if (ref.compareAndSet(curr, updated)) {
                return true;
            }
        }
    }
    public void cleanup() {
        long now = System.currentTimeMillis();
        clients.entrySet().removeIf(e -> now - e.getValue().get().windowStart >= windowMillis);
    }
}
