package com.example.pikamouse.learn_utils.tools.util;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: jiangfeng
 * @date: 2018/12/27
 */
public class ThreadUtil {
    /**
     * 线程池1
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final Executor THREAD_POOL_EXECUTOR;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private final AtomicInteger num = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread" + num.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>();

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    public static void executeInExcecutor(Runnable r) {
        THREAD_POOL_EXECUTOR.execute(r);
    }

    /**
     * 线程池2
     */
    private static ThreadPoolProxy mThreadPoolProxy;

    public static ThreadPoolProxy getThreadPoolProxy() {
        if (mThreadPoolProxy == null) {
            synchronized (ThreadUtil.class) {
                if (mThreadPoolProxy == null) {
                    mThreadPoolProxy = new ThreadPoolProxy(5, 5);
                }
            }
        }
        return mThreadPoolProxy;
    }

    private static class ThreadPoolProxy {
        private ThreadPoolExecutor mExecutor;
        private int mCorePoolSize;
        private int mMaximumPoolSize;

        public ThreadPoolProxy (int corePoolSize, int maximumPoolSize) {
            this.mCorePoolSize = corePoolSize;
            this.mMaximumPoolSize = maximumPoolSize;
        }

        private void initThreadPoolExecutor() {
            if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                synchronized (ThreadPoolProxy.class) {
                    if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
                        long keepAliveTime = 3000;
                        TimeUnit unit = TimeUnit.SECONDS;
                        BlockingQueue queue = new LinkedBlockingDeque();
                        ThreadFactory factory = Executors.defaultThreadFactory();
                        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
                        mExecutor = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, keepAliveTime, unit, queue, factory, handler);
                    }
                }
            }
        }

        public void excute(Runnable runnable) {
            initThreadPoolExecutor();
            mExecutor.execute(runnable);
        }
    }

    /**
     * 消息投递到主线程执行
     */
    private final static Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable r) {
        HANDLER.post(r);
    }
}
