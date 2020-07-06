package com.mgl.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: zhangqi
 * @CreateTime: 2020/7/518:54
 * @Company: MGL
 */
@Component
@Scope("prototype")
public class MyAsyncTaskClass extends Thread {
    private String str;

    @Override
    public void run() {

    }
}
