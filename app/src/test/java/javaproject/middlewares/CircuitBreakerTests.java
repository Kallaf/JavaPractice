package javaproject.middlewares;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CircuitBreakerTests {

    private static final int FAILURE_THRESHOLD = 3;
    private static final Duration OPEN_TIMEOUT = Duration.ofMillis(200);

    private static CircuitBreaker newBreaker() {
        return new CircuitBreaker(FAILURE_THRESHOLD, OPEN_TIMEOUT);
    }

    /** Drives the breaker into the OPEN state by recording enough failures. */
    private static CircuitBreaker openedBreaker() {
        CircuitBreaker breaker = newBreaker();
        for (int i = 0; i < FAILURE_THRESHOLD; i++) {
            breaker.recordFailure();
        }
        return breaker;
    }

    /** Drives the breaker into the HALF_OPEN state and consumes the trial permit. */
    private static CircuitBreaker halfOpenedBreaker() throws InterruptedException {
        CircuitBreaker breaker = openedBreaker();
        Thread.sleep(OPEN_TIMEOUT.toMillis() * 2);
        assertTrue(breaker.isRequestAllowed(), "trial call should be allowed after the open timeout");
        return breaker;
    }

    @Nested
    class ClosedState {

        @Test
        public void startsClosedAndAllowsRequests() {
            CircuitBreaker breaker = newBreaker();

            assertEquals(State.CLOSED, breaker.getState());
            assertTrue(breaker.isRequestAllowed());
            assertTrue(breaker.isRequestAllowed());
        }

        @Test
        public void staysClosedBelowFailureThreshold() {
            CircuitBreaker breaker = newBreaker();

            for (int i = 0; i < FAILURE_THRESHOLD - 1; i++) {
                breaker.recordFailure();
                assertEquals(State.CLOSED, breaker.getState());
                assertTrue(breaker.isRequestAllowed());
            }
        }

        @Test
        public void successResetsFailureCount() {
            CircuitBreaker breaker = newBreaker();

            breaker.recordFailure();
            breaker.recordFailure();
            breaker.recordSuccess();
            breaker.recordFailure();
            breaker.recordFailure();

            assertEquals(State.CLOSED, breaker.getState());
            assertTrue(breaker.isRequestAllowed());
        }
    }

    @Nested
    class OpenState {

        @Test
        public void opensWhenFailureThresholdIsReached() {
            CircuitBreaker breaker = openedBreaker();

            assertEquals(State.OPEN, breaker.getState());
        }

        @Test
        public void blocksRequestsWhileOpen() {
            CircuitBreaker breaker = openedBreaker();

            assertFalse(breaker.isRequestAllowed());
            assertFalse(breaker.isRequestAllowed());
            assertEquals(State.OPEN, breaker.getState());
        }

        @Test
        public void transitionsToHalfOpenAfterTimeout() throws InterruptedException {
            CircuitBreaker breaker = openedBreaker();

            Thread.sleep(OPEN_TIMEOUT.toMillis() * 2);

            assertTrue(breaker.isRequestAllowed());
            assertEquals(State.HALF_OPEN, breaker.getState());
        }
    }

    @Nested
    class HalfOpenState {

        @Test
        public void allowsOnlyASingleTrialCall() throws InterruptedException {
            CircuitBreaker breaker = halfOpenedBreaker();

            assertFalse(breaker.isRequestAllowed(), "second call must be blocked while the trial is in flight");
            assertEquals(State.HALF_OPEN, breaker.getState());
        }

        @Test
        public void successClosesTheCircuit() throws InterruptedException {
            CircuitBreaker breaker = halfOpenedBreaker();

            breaker.recordSuccess();

            assertEquals(State.CLOSED, breaker.getState());
            assertTrue(breaker.isRequestAllowed());
        }

        @Test
        public void failureReopensTheCircuitImmediately() throws InterruptedException {
            CircuitBreaker breaker = halfOpenedBreaker();

            breaker.recordFailure();

            assertEquals(State.OPEN, breaker.getState());
            assertFalse(breaker.isRequestAllowed());
        }
    }
}