package com.qcj.spring.framework.aop.intercept;

import com.qcj.spring.framework.aop.aspect.JoinPoint;
import com.sun.corba.se.spi.ior.ObjectKey;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by qcj on 2019-09-19
 */
public class QMethodInvocation implements JoinPoint {

    private Object proxy;

    private Method method;

    private Object target;

    private Object[] arguments;

    private List<Object> interceptorsAndDynamicMethodMatchers;

    private Class<?> targetClass;

    private int currentInterceptorIndex = -1;


    public QMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable {
        //	We start with an index of -1 and increment early.
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }

        Object interceptorOrInterceptionAdvice =
                this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof QMethodInterceptor) {
            // Evaluate dynamic method matcher here: static part will already have
            // been evaluated and found to match.
            QMethodInterceptor mi =
                    (QMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            // Dynamic matching failed.
            // Skip this interceptor and invoke the next in the chain.
            return proceed();
        }
    }

    public Object getThis() {
        return this.target;
    }

    public Object[] getArguments() {
        return this.arguments;
    }

    public Method getMethod() {
        return this.method;
    }
}

