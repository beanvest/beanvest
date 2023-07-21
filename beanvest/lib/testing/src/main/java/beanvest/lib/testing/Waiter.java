package beanvest.lib.testing;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

public class Waiter {
    final public static WaiterImpl NETWORK_LOCAL = Waiter.create(Duration.ofSeconds(2), Duration.ofMillis(100));
    final public static WaiterImpl NETWORK_REMOTE = Waiter.create(Duration.ofSeconds(10), Duration.ofMillis(900));

    final private static Duration DEFAULT_TIMEOUT = Duration.of(5, ChronoUnit.SECONDS);
    final private static Duration DEFAULT_RETRY_INTERVAL = Duration.of(300, ChronoUnit.MILLIS);
    final private static WaiterImpl WAITER = new WaiterImpl(DEFAULT_TIMEOUT, DEFAULT_RETRY_INTERVAL);

    public static WaiterImpl create(Duration timeout, Duration retryInterval) {
        return new WaiterImpl(timeout, retryInterval);
    }

    public static void waitFor(Runnable runnable) {
        WAITER.waitFor(runnable);
    }

    public static <T> T waitFor(Callable<T> callable) {
        return WAITER.waitFor(callable);
    }

    public static void justWaitABit() {
        WAITER.justWaitABit();
    }

    public static void sleep(Duration duration) {
        WAITER.sleep(duration);
    }
}
