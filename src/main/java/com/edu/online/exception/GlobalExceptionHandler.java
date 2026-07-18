package com.edu.online.exception;

import com.edu.online.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常统一捕获
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.edu.online.controller")
public class GlobalExceptionHandler {

    // 文件超过100MB捕获
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxSize() {
        log.error("上传文件最大限制100MB，请压缩后重新上传");
        return Result.fail(413, "上传文件最大限制100MB，请压缩后重新上传");
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常:{}", e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.warn("系统未知异常:{}", e);
        return Result.fail(500, "系统未知异常");
    }

}
