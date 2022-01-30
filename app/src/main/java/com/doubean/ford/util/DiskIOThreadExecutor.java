package com.doubean.ford.util;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Copied from https://raw.githubusercontent.com/android/architecture-samples/todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/util/DiskIOThreadExecutor.java
 * <p>
 * Executor that runs a task on a new background thread.
 */
public class DiskIOThreadExecutor implements Executor {

    private final Executor mDiskIO;

    public DiskIOThreadExecutor() {
        mDiskIO = Executors.newSingleThreadExecutor();
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mDiskIO.execute(command);
    }
}