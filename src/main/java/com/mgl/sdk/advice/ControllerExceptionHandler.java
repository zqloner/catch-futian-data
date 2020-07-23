package com.mgl.sdk.advice;

import com.mgl.sdk.dto.ResultDTO;
import com.mgl.sdk.exception.custom.MglRuntimeException;
import com.mgl.sdk.utils.ExceptionUtils;
import com.mgl.sdk.utils.StreamUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Springboot方式下对controller全局未捕获异常的处理
 *
 * @author fengweri
 */
@SuppressWarnings("rawtypes")
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger logger = LogManager.getLogger();

    @Value("${spring.application.name:未命名服务}")
    private String serverName;

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultDTO handler(HttpServletRequest req, Exception e) {
        logger.warn("接口异常：",e);
        ResultDTO result = new ResultDTO<>("处理出错,详情见提示信息.", ResultDTO.FAIL);
        MglRuntimeException mglRunTimeException = ExceptionUtils.getMglRunTimeException(e, 7);
        if (mglRunTimeException != null) {
            result.setExceptionCode(mglRunTimeException.getExceptionCode());
        }
        if (e instanceof MglRuntimeException) {
            String msg = ((MglRuntimeException) e).getMsg();
            result.setException(StringUtils.isNotEmpty(msg) ? msg : e.getMessage());
        } else {
            result.setException(StreamUtils.throwableFirstLine(e));
        }
        return result;
    }

}
