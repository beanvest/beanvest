package bb.lib.testing;

import org.opentest4j.AssertionFailedError;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.LockSupport;

public class WaiterImpl {
    final private Duration timeout;
    final private Duration retryInterval;

    public WaiterImpl(Duration timeout, Duration retryInterval) {
        this.retryInterval = retryInterval;
        this.timeout = timeout;
    }


    public void waitFor(Runnable runnable) {
        waitFor(() -> {
            runnable.run();
            return null;
        });
    }

    public <T> T waitFor(Duration timeout, Duration interval, Callable<T> callable) {
        var limit = System.currentTimeMillis() + timeout.toMillis();

        int attempts = 0;
        Throwable lastError = null;
        while (System.currentTimeMillis() < limit) {
            attempts += 1;
            try {
                return callable.call();
            } catch (AssertionError | Exception e) {
                lastError = e;
            }
            LockSupport.parkNanos(interval.toNanos());

        }
        throw new AssertionFailedError(lastError.getMessage() + "\nWaiter tried " + attempts + " times over " + timeout.toSeconds() + " seconds with no luck", lastError);
    }

    public <T> T waitFor(Callable<T> callable) {
        return waitFor(timeout, retryInterval, callable);
    }

    public void justWaitABit() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
