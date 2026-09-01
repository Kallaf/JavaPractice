package javaproject.config;

public class RateLimiterConfig {
    private final int maxRequests;
    private final int timeWindowInSeconds;

    public RateLimiterConfig(int maxRequests, int timeWindowInSeconds) {
        this.maxRequests = maxRequests;
        this.timeWindowInSeconds = timeWindowInSeconds;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public int getTimeWindowInSeconds() {
        return timeWindowInSeconds;
    }

    public int getLeakRate() {
        return maxRequests / timeWindowInSeconds;
    }
}