package com.lit.soullark.framework.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author myou
 * @Date 2020/7/2  11:04 上午
 */
public class FrameworkThreadFactory implements ThreadFactory {
    private static final AtomicLong threadNumber = new AtomicLong(1);
    private static final AtomicLong poolNumber = new AtomicLong(1);
    private final String prefix;
    private final boolean isDaemon;
    private final ThreadGroup threadGroup;

    public FrameworkThreadFactory(String prefix, boolean isDaemon) {
        this.prefix = prefix;
        this.isDaemon = isDaemon;
        this.threadGroup = System.getSecurityManager() == null ? Thread.currentThread().getThreadGroup() : System.getSecurityManager().getThreadGroup();
    }

    public FrameworkThreadFactory(String prefix) {
        this(prefix, false);
    }

    public FrameworkThreadFactory() {
        this(String.format("soul-lark-zk-framework-pool-%d", poolNumber.getAndIncrement()));
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(this.threadGroup, r, String.format("%s-thread-number-%d", this.prefix, threadNumber.getAndIncrement()));
        thread.setDaemon(this.isDaemon);
        return thread;
    }
}
