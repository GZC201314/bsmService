package org.bsm.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.utils.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 @author GZC
 */
@Aspect
@Component
@Slf4j
public class StatisticsQPSAspect {
    @Resource
    private RedisUtil redisUtil;

    @Pointcut("@annotation(org.bsm.annotation.StatisticsQPS)")
    private void anyMethod() {
    }

    @AfterReturning(pointcut = "anyMethod()")
    public void doBefore(JoinPoint joinPoint) {
        StatisticsQPS statisticsQPSAnnotation = getStatisticsQPSAnnotation(joinPoint);
        if (statisticsQPSAnnotation == null) {
            return;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //如果加了注解
        //获取方法名
        String functionName = signature.getDeclaringTypeName() + "." + signature.getName() + "()";
        //成功的QPS

        Object qps_success = redisUtil.getZsetKeyValue("QPS_success", functionName);

        double count = 0;
        if (qps_success != null) {
            count = (double) qps_success;
        }
        redisUtil.addZsetValue("QPS_success", functionName, count +1);
    }


    /**
     * 拦截异常操作，有异常时执行
     */
    @AfterThrowing(value = "anyMethod()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e) {
        StatisticsQPS statisticsQPSAnnotation = getStatisticsQPSAnnotation(joinPoint);
        if (statisticsQPSAnnotation == null) {
            return;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //如果加了注解
        //获取方法名
        String functionName = signature.getDeclaringTypeName() + "." + signature.getName() + "()";
        //成功的QPS

        String qps_fail = "QPS_fail";
        Object qpsFail = redisUtil.getZsetKeyValue(qps_fail, functionName);
        double count = 0;
        if (qpsFail != null) {
            count = (double) qpsFail;
        }
        redisUtil.addZsetValue(qps_fail,functionName, count +1);
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private static StatisticsQPS getStatisticsQPSAnnotation(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(StatisticsQPS.class);
        }
        return null;
    }

}
