package javaproject.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class RandomDelayGeneratorTests {

    @Test
    public void testDelayWithinRange() {
        int minDelay = 100;
        int maxDelay = 500;
        long startTime = System.currentTimeMillis();
        RandomDelayGenerator.delay(minDelay, maxDelay);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        assertTrue(elapsedTime >= minDelay && elapsedTime <= maxDelay,
         "Delay should be within the specified range");
    }

}