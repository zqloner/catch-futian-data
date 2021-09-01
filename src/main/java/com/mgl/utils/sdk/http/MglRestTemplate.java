package com.mgl.utils.sdk.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @Description restTemplate缺省实现
 * @Author fengwei
 * @Date 2020/7/22 9:47
 * @Version 1.0
 */
@Component
public class MglRestTemplate extends RestTemplate implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 配置单位：秒
     */
    @Value("${httpclient.timeout.connection:2}")
    private int connectTimeout;

    @Value("${httpclient.timeout.read:8}")
    private int readTimeout;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout * 1000);
        requestFactory.setReadTimeout(readTimeout * 1000);
        setRequestFactory(requestFactory);
    }

    public int getReadTimeout(){
        return readTimeout;
    }
}
