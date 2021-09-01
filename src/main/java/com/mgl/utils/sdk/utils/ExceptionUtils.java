package com.mgl.utils.sdk.utils;

import com.mgl.utils.sdk.exception.custom.MglRuntimeException;

/**
 * @description:
 * @author: fengwei
 **/
public class ExceptionUtils {

    public static MglRuntimeException getMglRunTimeException(Throwable e, Integer times){
        MglRuntimeException result = null;
        if(e instanceof MglRuntimeException
                && ((MglRuntimeException) e).getExceptionCode() != null){
            result = (MglRuntimeException) e;
            return result;
        }
        if(e.getCause() != null && times>0){
            result = getMglRunTimeException(e.getCause(),times-1);
        }
        return result;
    }
}
