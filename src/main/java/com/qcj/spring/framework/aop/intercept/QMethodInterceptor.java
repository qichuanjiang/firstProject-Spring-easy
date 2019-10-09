package com.qcj.spring.framework.aop.intercept;

/**
 * Created by qcj on 2019-09-19
 */
public interface QMethodInterceptor {

        Object invoke(QMethodInvocation invocation) throws Throwable;

}
