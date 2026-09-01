package javaproject.middlewares;

public interface IRateLimiterMiddleware {
    public boolean isRequestAllowed(String clientId);
}