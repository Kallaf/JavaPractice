package javaproject.middlewares;

public interface ICircuitBreaker {
    boolean isRequestAllowed();
    void recordFailure();
    void recordSuccess();
}