package javaproject.utils;

public class RandomDelayGenerator {
    public static void delay(int minMilliseconds, int maxMilliseconds) {
        int delay = minMilliseconds + (int) (Math.random() * (maxMilliseconds - minMilliseconds));
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}