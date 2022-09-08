package org.bsm.annotation;

import java.lang.annotation.*;

/**
 * @author GZC
 * @create 2022-05-31 20:19
 * @desc 刷新Session
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefreshSession {
    String value() default "";
}
