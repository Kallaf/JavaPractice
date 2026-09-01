package javaproject.middlewares;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

// Enum for Circuit Breaker states
enum State {
    CLOSED,     // Normal operation
    OPEN,       // Calls are blocked
    HALF_OPEN   // Test calls allowed
}

class CircuitBreaker implements ICircuitBreaker {
    private final ReentrantLock lock = new ReentrantLock();
    private final int failureThreshold;
    private final Duration openStateTimeout;

    // All mutable state below is guarded by 'lock'.
    private State state = State.CLOSED;
    private int failureCount = 0;
    private Instant lastFailureTime;
    private boolean probeInFlight = false;

    public CircuitBreaker(int failureThreshold, Duration openStateTimeout) {
        this.failureThreshold = failureThreshold;
        this.openStateTimeout = openStateTimeout;
    }

    @Override
    public boolean isRequestAllowed() {
        lock.lock();
        try {
            switch (state) {
                case OPEN:
                    if (lastFailureTime != null
                            && Duration.between(lastFailureTime, Instant.now()).compareTo(openStateTimeout) > 0) {
                        state = State.HALF_OPEN; // Try again after timeout
                        probeInFlight = true;
                        return true; // allow a single trial call
                    }
                    return false;
                case HALF_OPEN:
                    if (probeInFlight) {
                        return false; // a trial call is already in progress
                    }
                    probeInFlight = true;
                    return true;
                case CLOSED:
                default:
                    return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void recordSuccess() {
        lock.lock();
        try {
            failureCount = 0;
            lastFailureTime = null;
            probeInFlight = false;
            state = State.CLOSED;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void recordFailure() {
        lock.lock();
        try {
            lastFailureTime = Instant.now();
            probeInFlight = false;
            if (state == State.HALF_OPEN) {
                state = State.OPEN; // trial call failed, re-open immediately
                return;
            }
            failureCount++;
            if (failureCount >= failureThreshold) {
                state = State.OPEN;
            }
        } finally {
            lock.unlock();
        }
    }

    public State getState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }
}