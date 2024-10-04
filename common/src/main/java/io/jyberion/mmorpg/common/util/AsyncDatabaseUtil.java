package io.jyberion.mmorpg.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncDatabaseUtil {

    private static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(10);

    public static ExecutorService getExecutor() {
        return databaseExecutor;
    }

    public static void shutdown() {
        databaseExecutor.shutdown();
    }
}
