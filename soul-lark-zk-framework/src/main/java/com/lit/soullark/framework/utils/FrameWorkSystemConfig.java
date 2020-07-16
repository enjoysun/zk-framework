package com.lit.soullark.framework.utils;

/**
 * @Author myou
 * @Date 2020/6/30  11:06 上午
 */
public class FrameWorkSystemConfig {
    // core线程数量
    public static final int SYSTEM_KERNEL = Math.max(4, Runtime.getRuntime().availableProcessors() * 2);
    // 非核心空闲存活时间(单位ms，查看ThreadPool设置)
    public static final long KEEP_ALIVE_TIME = 4000L;
    // 默认线程池数量
    public static final int THREAD_POOL_THREAD_COUNT = Integer.getInteger("thread.pool.default.thread.capacity", SYSTEM_KERNEL);
    // 默认线程池任务队列可容纳元素(为0时：SynchronousQueue类型队列 小于0时: LinkedBlockingQueue)
    public static final int THREAD_POOL_QUEUE_ELEMENT = Integer.getInteger("thread.pool.default.queue.element", 0);
    // 全局业务计算等待时间
    public static final int AWAIT_TIME = 10 * 1000;
}
