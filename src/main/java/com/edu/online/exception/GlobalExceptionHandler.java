package com.edu.online.exception;

import com.edu.online.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常统一捕获
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.edu.online.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常:{}", e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 捕获系统未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.warn("系统未知异常:{}", e);
        return Result.fail(500, "系统未知异常");
    }

}
