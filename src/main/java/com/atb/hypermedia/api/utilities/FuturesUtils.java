package com.atb.hypermedia.api.utilities;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utils for {@link Future}
 */
public class FuturesUtils {

    private static final Logger logger = LoggerFactory.getLogger(FuturesUtils.class);

    /**
     * Calls Future.get and returns the result or the provided fallback.
     *
     * @param future {@link Future} of type V
     * @param fallback the fallback value of type V.
     * @return V
     */
    public static <V> V getWithFallback(Future<V> future, V fallback) {
        try {
            return future.get();
        } catch (Exception e) {
            logger.error("FutureUtils.getWithFallback returning fallback", e);
            return fallback;
        }
    }

    /**
     * Get value from a Future and propagate all exceptions.
     * ExecutionExceptions will have their cause extracted and propagated.
     * Checked exceptions will be wrapped in a RuntimeException
     *
     * Generally, you will want to wrap this in your asynchronous services
     * rather than use it directly.
     *
     * This will replace FutureUtils.getWithException when usage of that is fully
     * phased out.
     *
     * @param future {@link Future} of type V
     * @return V
     */
    public static <V> V getWithPropagatedException(Future<V> future) {
        try {
            return future.get();
        } catch (Throwable t) {
            handleGetExceptions(t);
            // will never reach here
            return null;
        }
    }

    public static <V> V getWithPropagatedException(Future<V> future, long timeout, TimeUnit timeUnit) {
        try {
            return future.get(timeout, timeUnit);
        } catch (Throwable t) {
            handleGetExceptions(t);
            // will never reach here
            return null;
        }
    }

    private static void handleGetExceptions(Throwable t) {
        if (t instanceof ExecutionException) {
            throw Throwables.propagate(t.getCause());
        } else {
            throw Throwables.propagate(t);
        }
    }
}
