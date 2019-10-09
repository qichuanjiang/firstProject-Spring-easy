package com.qcj.spring.framework.aop.aspect;

import com.qcj.spring.framework.aop.intercept.QMethodInterceptor;
import com.qcj.spring.framework.aop.intercept.QMethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qcj on 2019-09-19
 */
public class QAfterAdvice extends QAbstractAdvice implements QMethodInterceptor {

    private JoinPoint joinPoint;

    public QAfterAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public Object invoke(QMethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        this.joinPoint = invocation;
        this.after(retVal,invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return retVal;
    }

    private void after(Object retVal, Method method, Object[] arguments, Object aThis) throws InvocationTargetException, IllegalAccessException {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
