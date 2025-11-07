package io.github.ajcode404.wise;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

enum State {
    CLOSED,OPEN,HALF_OPEN;
}

public class CircuitBreaker {

    private final int failureThreshold;
    private final long openStateTimeoutMillis;
    private final long failureTimeoutWindowMillis;

    private final AtomicInteger fc = new AtomicInteger(0);
    private final AtomicLong lft = new AtomicLong(0);
    private final AtomicLong osea = new AtomicLong(0);

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);

    public CircuitBreaker(int failureThreshold, long openStateTimeoutMillis,  long failureTimeWindowMillis) {
        this.failureThreshold = failureThreshold;
        this.openStateTimeoutMillis = openStateTimeoutMillis;
        this.failureTimeoutWindowMillis = failureTimeWindowMillis;
    }

    public void call(RemoteService rs) throws Exception {
        long now = System.currentTimeMillis();
        switch (state.get()) {
            case OPEN -> {
                if (now - osea.get() > openStateTimeoutMillis) {
                    state.set(State.HALF_OPEN);
                    System.out.println("CircuitBreaker -> Moving to HALF_OPEN state");
                } else {
                    throw new RuntimeException("CB OPEN");
                }
            }
            case HALF_OPEN -> {
                try {
                    rs.call();
                    reset();
                } catch (Exception e) {
                    trip();
                    throw e;
                }
            }
            case CLOSED -> {
                try {
                    rs.call();
                    reset();
                } catch (Exception ex) {
                    recordFailure();
                    throw ex;
                }
            }
        }
    }

    private void recordFailure() {
        long now = System.currentTimeMillis();
        if (now - lft.get() > failureTimeoutWindowMillis) {
            fc.set(0);
        }
        lft.set(now);
        int failures = fc.incrementAndGet();
        if (failures >= failureThreshold) {
            trip();
        }
    }

    private void reset() {
        fc.set(0);
        lft.set(0);
        state.set(State.CLOSED);
    }

    private void trip() {
        state.set(State.OPEN);
        osea.set(System.currentTimeMillis());
        System.out.println("CB -> Tripped to open state");
    }


    public static void main(String[] args) throws InterruptedException {
        CircuitBreaker cb = new CircuitBreaker(3, 1000, 10000);

        RemoteService rs = () -> {
            if (Math.random() < 0.6) throw new RuntimeException("Service not available");
            System.out.println("Success");
        };
        for (int i = 0; i < 100; i++) {
            try {
                cb.call(rs);
            } catch (Exception e) {
                System.out.println("Call " + i + " -> " + e.getMessage());
            }
            Thread.sleep(500);
        }
    }
}
