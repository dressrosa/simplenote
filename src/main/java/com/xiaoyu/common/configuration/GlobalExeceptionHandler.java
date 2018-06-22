package com.xiaoyu.common.configuration;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiaoyu.common.base.ResponseCode;
import com.xiaoyu.common.base.ResponseMapper;

/**
 * @author hongyu
 * @date 2018-06
 * @description 异常拦截,server请求收到,但是client取消,导致死循环栈溢出
 *              Resolved exception caused by Handler execution:
 *              org.apache.catalina.connector.ClientAbortException:
 *              java.io.IOException: Broken pipe
 *              //这里捕获异常其实已经栈溢出了,所以只是为了避免大量日志,
 *              //交给 {@link EncodeFilterConfiguration} dofilter方法里面进行catch
 *              
 */
@Deprecated 
//@ControllerAdvice
public class GlobalExeceptionHandler {
    private final static Logger LOG = LoggerFactory.getLogger("GlobalExeceptionHandler");

//    @ExceptionHandler(IOException.class)
//    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Object exceptionHandler(IOException e, HttpServletRequest request) {
        if (e instanceof ClientAbortException) {
            LOG.error("catch ClientAbortException:Broken pipe");
            return ResponseMapper.createMapper().code(ResponseCode.FAILED.statusCode()).resultJson();
        }
        return null;
    }
}
