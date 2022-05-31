package org.bsm.annotation;

import java.lang.annotation.*;

/**
 * @author GZC
 * @create 2022-05-31 20:19
 * @desc 统计QPS
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StatisticsQPS {
    String value() default "";
}
