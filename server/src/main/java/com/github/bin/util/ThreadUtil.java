package com.github.bin.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author bin
 * @since 2023/09/20
 */
public class ThreadUtil {
    private static final ScheduledThreadPoolExecutor EXECUTOR;
    private static final AtomicInteger THREAD_COUNT = new AtomicInteger();

    static {
        final ThreadGroup threadGroup = new ThreadGroup("AsyncPool");
        final ThreadFactory FACTORY = r -> {
            final Thread thread = new Thread(threadGroup, r, "AsyncPool" + THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        };
        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        EXECUTOR = new ScheduledThreadPoolExecutor(availableProcessors, FACTORY);
        EXECUTOR.setKeepAliveTime(1, TimeUnit.MINUTES);
    }

    public static <T> void execute(T t, Consumer<T> consumer) {
        EXECUTOR.execute(() -> consumer.accept(t));
    }
}
