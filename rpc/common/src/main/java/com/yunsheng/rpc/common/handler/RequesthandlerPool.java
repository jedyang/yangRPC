package com.yunsheng.rpc.common.handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 业务处理线程池
 *
 * @author yunsheng
 */
public class RequesthandlerPool {
    private static ThreadPoolExecutor poolExecutor;

    public static void submit(Runnable task) {
        if (null == poolExecutor) {
            synchronized (RequesthandlerPool.class) {
                if (null == poolExecutor) {
                    poolExecutor = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
                }
            }
        }
        poolExecutor.submit(task);
    }
}
