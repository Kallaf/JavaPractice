package javaproject.middlewares;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import javaproject.config.RateLimiterConfig;
import javaproject.utils.RandomDelayGenerator;

public class RateLimiterMiddlewareTests {
    private final RateLimiterConfig config = new RateLimiterConfig(5, 1); // Allow 5 requests per 1 second
    private final IRateLimiterMiddleware rateLimiter = new LeakyBucketRateLimiterMiddleware(config);
    private final String clientId = "test-client";
    
    @Test
    public void testLeakyBucketRateLimiter() {
        for (int i = 0; i < 7; i++) {
            boolean allowed = rateLimiter.isRequestAllowed(clientId);
            if (i < 5) {
                assertTrue(allowed, "Request " + (i + 1) + " should be allowed");
            } else {
                assertTrue(!allowed, "Request " + (i + 1) + " should be denied");
            }
        }
    }

    @Test
    public void testLeakyBucketRateLimiterWithDelay() {
        for (int i = 0; i < 7; i++) {
            boolean allowed = rateLimiter.isRequestAllowed(clientId);
            assertTrue(allowed, "Request " + (i + 1) + " should be allowed after delay");
            RandomDelayGenerator.delay(250, 300);
        }
    }
}