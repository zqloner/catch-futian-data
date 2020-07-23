package com.mgl.sdk.thread;

import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @description:
 **/
public class MglRunnable implements Runnable {
    private Runnable task;
    private RequestAttributes context;
    private String logId;

    public MglRunnable(Runnable task, RequestAttributes context, String logId) {
        this.task = task;
        this.context = context;
        this.logId = logId;
    }

    @Override
    public void run() {
        if (context != null) {
            RequestContextHolder.setRequestAttributes(context);
        }
        MDC.put("logId", logId);
        try {
            task.run();
        } finally {
            RequestContextHolder.resetRequestAttributes();
            MDC.clear();
        }
    }
}