package io.jyberion.mmorpg.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class RetryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    public static <T> T retry(Supplier<T> action, int maxRetries, long delayMillis) throws Exception {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return action.get();
            } catch (Exception e) {
                attempt++;
                logger.warn("Attempt {} failed: {}", attempt, e.getMessage());
                if (attempt >= maxRetries) {
                    throw e;
                }
                Thread.sleep(delayMillis);
            }
        }
        throw new Exception("Operation failed after " + maxRetries + " attempts");
    }
}
