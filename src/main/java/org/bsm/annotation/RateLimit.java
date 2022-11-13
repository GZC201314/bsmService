package org.bsm.annotation;

import java.lang.annotation.*;

/**
 * @author GZC
 * @create 2022-11-13 16:15
 * @desc 限流
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
}
