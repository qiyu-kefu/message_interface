package com.qiyukf.openapi.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AsyncTaskManager {

    private ExecutorService executor;

    private int count;

    public AsyncTaskManager(final String threadName) {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName(threadName + (++count));
                return t;
            }
        });
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public void shutdownNow() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}