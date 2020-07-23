package com.mgl.sdk.thread;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @description:
 **/
public class MglTaskPoolExecutor extends ThreadPoolTaskExecutor {

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if(RequestContextHolder.getRequestAttributes() != null){
            return super.submit(new MglCallable(task, RequestContextHolder.getRequestAttributes(), MDC.get("logId")));
        }
        return super.submit(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        if(RequestContextHolder.getRequestAttributes() != null){
            return super.submitListenable(new MglCallable(task, RequestContextHolder.getRequestAttributes(), MDC.get("logId")));
        }
        return super.submitListenable(task);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(new MglRunnable(task, RequestContextHolder.getRequestAttributes(), MDC.get("logId")));
    }
}
