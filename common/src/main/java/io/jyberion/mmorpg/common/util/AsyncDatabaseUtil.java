package io.jyberion.mmorpg.common.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncDatabaseUtil {
    private static final ExecutorService dbExecutor = Executors.newFixedThreadPool(10);

    public static <T> CompletableFuture<T> runAsync(DatabaseTask<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, dbExecutor);
    }

    @FunctionalInterface
    public interface DatabaseTask<T> {
        T execute() throws Exception;
    }
}
