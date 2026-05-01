package JPA_Project.service;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Singleton Thread Pool Manager cho ứng dụng.
 * Sử dụng ExecutorService để quản lý các tác vụ đa luồng.
 */
public class ExecutorServiceManager {
    private static final int THREAD_POOL_SIZE = 10;
    private static ExecutorService executor;
    private static final Object LOCK = new Object();

    private ExecutorServiceManager() {
    }

    public static ExecutorService getInstance() {
        if (executor == null) {
            synchronized (LOCK) {
                if (executor == null) {
                    executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
                }
            }
        }
        return executor;
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return getInstance().submit(task);
    }

    public static void submit(Runnable task) {
        getInstance().submit(task);
    }

    public static void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public static int getActiveCount() {
        if (executor instanceof java.util.concurrent.ThreadPoolExecutor) {
            return ((java.util.concurrent.ThreadPoolExecutor) executor).getActiveCount();
        }
        return -1;
    }
}
