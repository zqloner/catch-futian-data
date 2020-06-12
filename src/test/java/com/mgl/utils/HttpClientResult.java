package com.mgl.utils;

import java.io.Serializable;

/**
 * @Title: HttpClientResult
 * @Description:
 * @Company: 盟固利
 * @author: 张奇
 * @date: ceate in 2020/6/5 18:39
 */
public class HttpClientResult implements Serializable {

        /**
         * 响应状态码
         */
        private int code;

        /**
         * 响应数据
         */
        private String content;


    public HttpClientResult() {
    }

    public HttpClientResult(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public HttpClientResult(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
