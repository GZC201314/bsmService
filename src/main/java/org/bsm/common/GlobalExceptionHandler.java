package org.bsm.common;

import lombok.extern.slf4j.Slf4j;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(value = BsmException.class)
    public ResponseResult<String> bizExceptionHandler(HttpServletRequest req, BsmException e){
        log.error("发生业务异常！原因是：{}",e.getErrorMsg());
        return Response.makeErrRsp(e.getMessage());
    }

    /**
     * 处理空指针的异常
     */
    @ExceptionHandler(value =NullPointerException.class)
    public ResponseResult<String> exceptionHandler(HttpServletRequest req, NullPointerException e){
        log.error("发生空指针异常！原因是:",e);
        return Response.makeErrRsp(e.getMessage());
    }


    /**
     * 处理其他异常
     */
    @ExceptionHandler(value =Exception.class)
    public ResponseResult<String> exceptionHandler(HttpServletRequest req, Exception e){
        log.error("未知异常！原因是:",e);
        return Response.makeErrRsp(e.getMessage());
    }
}
