package com.mgl.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/7/518:26
 * @Company: MGL
 */

@EnableAsync
public class AsyncTaskConfig implements AsyncConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskConfig.class);

    /**
     * ThredPoolTaskExcutor的处理流程 当池子大小小于corePoolSize，就新建线程，并处理请求
     * 当池子大小等于corePoolSize，把请求放入workQueue中，池子里的空闲线程就去workQueue中取任务并处理
     * 当workQueue放不下任务时，就新建线程入池，并处理请求，如果池子大小撑到了maximumPoolSize，就用RejectedExecutionHandler来做拒绝处理
     * 当池子的线程数大于corePoolSize时，多余的线程会等待keepAliveTime长时间，如果无请求可处理就自行销毁
     */

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // 最小线程数(核心线程数)
        taskExecutor.setCorePoolSize(4);
        // 最大线程数
        taskExecutor.setMaxPoolSize(8);
        // 等待队列(队列最大长度)
        taskExecutor.setQueueCapacity(16);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SpringAsyncExceptionHandler();
    }

    class SpringAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
            logger.error("Exception occurs in async method", throwable.getMessage());
        }
    }
}
