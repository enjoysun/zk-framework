package com.lit.soullark.framework.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author myou
 * @Date 2020/6/30  11:07 上午
 */
public class FrameworkRejectedExecution implements RejectedExecutionHandler {
    private Logger log = LoggerFactory.getLogger(FrameworkRejectedExecution.class.toString());

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.info(String.format("线程队列满载,采用任务提交线程处理任务，处理任务线程编号:%s-%s", Thread.currentThread().getName(), Thread.currentThread().getId()));
        new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(r, executor);
    }
}
