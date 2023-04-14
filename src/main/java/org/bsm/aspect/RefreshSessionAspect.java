package org.bsm.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.bsm.annotation.RefreshSession;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author GZC
 */
@Aspect
@Component
@Slf4j
public class RefreshSessionAspect {
    @Resource
    private RedisUtil redisUtil;

    @Autowired
    private HttpSession session;

    @Pointcut("@annotation(org.bsm.annotation.RefreshSession)")
    private void anyMethod() {
    }

    @AfterReturning(pointcut = "anyMethod()")
    public void doBefore(JoinPoint joinPoint) {
        RefreshSession refreshSessionAnnotation = getRefreshSessionAnnotation(joinPoint);
        if (refreshSessionAnnotation == null) {
            return;
        }
        String id = session.getId();
        Map<Object, Object> hmget = redisUtil.hmget(id);
        log.info("用户sessionID = {}", id);
        if (hmget.isEmpty()) {
            throw new RuntimeException("没有查询到当前用户的登录状态，请重新登录。");
        }
        int sessionTimeout = Integer.parseInt((String) redisUtil.hget("bsm_config", "SESSION_TIMEOUT"));
        redisUtil.expire(id, sessionTimeout);
    }


    /**
     * 拦截异常操作，有异常时执行
     */
    @AfterThrowing(value = "anyMethod()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e) {
        RefreshSession refreshSessionAnnotation = getRefreshSessionAnnotation(joinPoint);
        if (refreshSessionAnnotation == null) {
            return;
        }
        String id = session.getId();
        log.info("用户sessionID = {}", id);
        Map<Object, Object> hmget = redisUtil.hmget(id);
        if (hmget.isEmpty()) {
            throw new RuntimeException("没有查询到当前用户的登录状态，请重新登录。");
        }
        int sessionTimeout = Integer.parseInt((String) redisUtil.hget("bsm_config", "SESSION_TIMEOUT"));
        redisUtil.expire(id, sessionTimeout);
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private static RefreshSession getRefreshSessionAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(RefreshSession.class);
        }
        return null;
    }

}
