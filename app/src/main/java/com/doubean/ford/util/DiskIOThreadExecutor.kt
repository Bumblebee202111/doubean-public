package com.doubean.ford.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Copied from https://raw.githubusercontent.com/android/architecture-samples/todo-mvvm-live/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/util/DiskIOThreadExecutor.java
 *
 * Executor that runs a task on a new background thread.
 */
class DiskIOThreadExecutor : Executor {
    private val mDiskIO: Executor

    init {
        mDiskIO = Executors.newSingleThreadExecutor()
    }

    override fun execute(command: Runnable) {
        mDiskIO.execute(command)
    }
}