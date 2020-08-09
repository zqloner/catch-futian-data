package com.mgl.sdk.thread;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description TODO
 * @Author fengwei
 * @Date 2020/7/23 8:04
 * @Version 1.0
 */
public class MglThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 线程池构造方法
     * @param corePoolSize 核心线程数量
     * @param maximumPoolSize 最大线程数量
     * @param keepAliveTime 存活时间（单位：秒）
     * @param threadName 线程名字
     */
    public MglThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,String threadName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime,TimeUnit.SECONDS,new ArrayBlockingQueue<>(2000),
                new InnerThreadFactory(threadName),(Runnable r, ThreadPoolExecutor executor) -> {
                    try {
                        executor.getQueue().put(r);
                    } catch (Exception e) {
                        throw new RuntimeException("将线程加入队列时错误！",e);
                    }
                });
    }

    /**
     * 私有ThreadFactory实现类
     */
    private static class InnerThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup threadGroup;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public InnerThreadFactory(String name){
            SecurityManager securityManager =System.getSecurityManager();
            threadGroup = (securityManager != null) ? securityManager.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            if (StringUtils.isBlank(name)) {
                name = "mgl";
            }
            namePrefix =name + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(threadGroup,r,namePrefix + threadNumber.getAndIncrement(),0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}
