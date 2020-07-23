package com.mgl.sdk.thread;

import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * @description:
 **/
public class MglCallable<T> implements Callable<T> {
    private Callable<T> task;
    private RequestAttributes context;
    private String logId;

    public MglCallable(Callable<T> task, RequestAttributes context, String logId) {
        this.task = task;
        this.context = context;
        this.logId = logId;
    }

    @Override
    public T call() throws Exception {
        if (context != null) {
            RequestContextHolder.setRequestAttributes(context);
        }
        MDC.put("logId", logId);
        try {
            return task.call();
        } finally {
            RequestContextHolder.resetRequestAttributes();
            MDC.clear();
        }
    }
}