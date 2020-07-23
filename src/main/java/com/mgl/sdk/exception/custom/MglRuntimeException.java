package com.mgl.sdk.exception.custom;

/**
 * @Description 自定义运行异常
 * @Author fengwei
 * @Date 2020/7/23 8:26
 * @Version 1.0
 */
public class MglRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -725047124810186410L;

    private String msg;

    private String bizNo;

    private String exceptionCode;

    public MglRuntimeException() {
        super();
    }

    public MglRuntimeException(String msg) {
        super(msg);
    }

    public MglRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MglRuntimeException(Throwable cause) {
        super(cause);
    }

    public MglRuntimeException(String msg, String bizNo){
        this.bizNo = bizNo;
        this.msg = msg;
    }

    public MglRuntimeException(String exceptionCode, String msg, Throwable cause){
        super(cause);
        this.exceptionCode = exceptionCode;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getBizNo() {
        return bizNo;
    }

    public void setBizNo(String bizNo) {
        this.bizNo = bizNo;
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
