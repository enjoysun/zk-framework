package com.lit.soullark.framework.utils;

import java.util.concurrent.*;

/**
 * @Author myou
 * @Date 2020/6/30  11:09 上午
 */
public class FrameworkThreadPool {
    private static class GeneratePool {
        public static ExecutorService executorService(int coreThreads, int queues) {
            return new ThreadPoolExecutor(coreThreads,
                    FrameWorkSystemConfig.SYSTEM_KERNEL,
                    FrameWorkSystemConfig.KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    queues == 0 ? new SynchronousQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(),
                    new FrameworkThreadFactory(),
                    new FrameworkRejectedExecution()
            );
        }
    }

    public static ExecutorService getExecutorService() {
        return GeneratePool.executorService(FrameWorkSystemConfig.THREAD_POOL_THREAD_COUNT, FrameWorkSystemConfig.THREAD_POOL_QUEUE_ELEMENT);
    }
}
