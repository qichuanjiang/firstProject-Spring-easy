package com.qcj.spring.framework.aop.aspect;

import com.qcj.spring.framework.aop.intercept.QMethodInterceptor;
import com.qcj.spring.framework.aop.intercept.QMethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qcj on 2019-09-19
 */
public class QBeforeAdvice extends QAbstractAdvice implements QMethodInterceptor,QAdvice {

    private JoinPoint joinPoint;

    public QBeforeAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    private void before(Method method,Object[] args,Object target) throws InvocationTargetException, IllegalAccessException {
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }

    public Object invoke(QMethodInvocation invocation) throws Throwable {
        this.joinPoint = invocation;
        before(invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return invocation.proceed();
    }
}
