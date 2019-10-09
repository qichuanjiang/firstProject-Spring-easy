package com.qcj.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * Created by qcj on 2019-09-19
 */
public interface JoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();
}
