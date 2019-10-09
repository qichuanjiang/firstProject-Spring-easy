package com.qcj.spring.framework.aop.aspect;

import com.qcj.spring.framework.aop.intercept.QMethodInterceptor;
import com.qcj.spring.framework.aop.intercept.QMethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by qcj on 2019-09-19
 */
public class QAfterThrowAdvice extends QAbstractAdvice implements QMethodInterceptor {

    private String throwName;

    public QAfterThrowAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public Object invoke(QMethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(invocation,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwName = throwName;
    }
}
