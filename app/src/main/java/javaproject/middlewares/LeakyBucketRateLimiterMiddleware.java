package javaproject.middlewares;
import javaproject.config.RateLimiterConfig;

public class LeakyBucketRateLimiterMiddleware implements IRateLimiterMiddleware {
    private final RateLimiterConfig config;
    private int currentRequests;
    private long lastRequestTime;

    public LeakyBucketRateLimiterMiddleware(RateLimiterConfig config) {
        this.config = config;
        currentRequests = 0;
        lastRequestTime = System.currentTimeMillis();
    }

    @Override
    public boolean isRequestAllowed(String clientId) {
        long currentTime = System.currentTimeMillis();
        double elapsedTime = currentTime - lastRequestTime;

        int leakedRequests = (int) ((elapsedTime / 1000) * config.getLeakRate());
        currentRequests = Math.max(0, currentRequests - leakedRequests);

        if (currentRequests < config.getMaxRequests()) {
            currentRequests++;
            lastRequestTime = currentTime;
            return true;
        }
        return false;
    }
}