package com.mgl.sdk.dto;


import com.mgl.sdk.utils.NumberUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * controller通用返回对象 Created by huangfei-lhq on 2018/1/30.
 *
 * @author fengwei
 */
@SuppressWarnings("unchecked")
public class ResultDTO<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1396747744137479898L;
    /**
     * 接口返回成功代码
     */
    public static final int SUCCESS = 1;
    /**
     * 接口返回失败代码
     */
    public static final int FAIL = 0;

    public static final String CODESTRING = "code";
    public static final String DATASTRING = "data";
    public static final String MSGSTRING = "msg";

    /**
     * 默认返回信息
     */
    private String msg = "success";

    /**
     * 异常信息
     */
    private String exception;
    /**
     * 异常编码
     */
    private String exceptionCode;

    /**
     * 默认返回成功
     */
    private int code = SUCCESS;
    /**
     * 返回数据
     */
    private T data;

    public ResultDTO() {
        super();
    }

    public ResultDTO(Collection<? extends Serializable> data) {
        this.data = (T) data;
    }

    public ResultDTO(Map<? extends Serializable, ? extends Serializable> data) {
        this.data = (T) data;
    }

    /**
     * 返回成功
     *
     * @param data
     */
    public ResultDTO(T data) {
        super();
        this.data = data;
    }

    /**
     * 异常返回
     *
     * @param e
     */
    public ResultDTO(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = FAIL;
    }

    /**
     * 自定义
     *
     * @param msg
     */
    public ResultDTO(String msg, int code) {
        super();
        this.msg = msg;
        this.code = code;
    }

    public ResultDTO(String msg, int code, String exceptionCode) {
        super();
        this.msg = msg;
        this.code = code;
        this.exceptionCode = exceptionCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean ok() {
        return code == SUCCESS;
    }

    public boolean failed() {
        return code == FAIL;
    }

    public boolean noData() {
        return failed() || NumberUtils.isNoneValue(data);
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    @Override
    public String toString() {
        return "ResultDTO [msg=" + msg + ", exception=" + exception + ", code=" + code + ", data=" + data + ", exceptionCode="+exceptionCode+"]";
    }
}
