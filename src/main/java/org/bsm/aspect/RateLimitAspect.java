package org.bsm.aspect;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.bsm.utils.Response;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


/**
 * @author GZC
 * @create 2022-11-13 16:17
 * @desc
 */
@Component
@Scope
@Aspect
public class RateLimitAspect {

    @Resource
    private HttpServletResponse response;
    /**
     * 比如说，我这里设置"并发数"为5
     */
    private final RateLimiter rateLimiter = RateLimiter.create(6, 10, TimeUnit.SECONDS);

    @Pointcut("@annotation(org.bsm.annotation.RateLimit)")
    public void serviceLimit() {

    }

    @Around("serviceLimit()")
    public Object around(ProceedingJoinPoint joinPoint) {
        boolean flag = rateLimiter.tryAcquire();
        Object obj = null;
        try {
            if (flag) {
                obj = joinPoint.proceed();
            } else {
                String result = JSON.toJSONString(Response.makeErrRsp("请求失败！"));
                output(response, result);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void output(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

}
