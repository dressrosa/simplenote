/**
 * 
 */
package com.xiaoyu.simplenote.modules.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.xiaoyu.simplenote.common.base.ResponseCode;
import com.xiaoyu.simplenote.common.base.ResponseMapper;

/**
 * 全局异常处理
 * 
 * @author hongyu
 * @date 2019-04
 * @description
 */
@RestControllerAdvice
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception e) {
        logger.error("全局异常拦截:" + e);
        return ResponseMapper.createMapper()
                .code(ResponseCode.REQ_ERROR.statusCode())
                .resultJson();
    }
}
