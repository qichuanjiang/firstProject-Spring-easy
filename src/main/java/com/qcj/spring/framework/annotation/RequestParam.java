package com.qcj.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * Created by qcj on 2019-09-09
 */

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
