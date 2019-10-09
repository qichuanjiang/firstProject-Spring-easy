package com.qcj.spring.framework.aop;

import com.qcj.spring.framework.aop.intercept.QMethodInvocation;
import com.qcj.spring.framework.aop.support.QAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by qcj on 2019-09-18
 */
public class JdkProxy implements QAopProxy , InvocationHandler {

    private QAdvisedSupport advised;

    public JdkProxy(QAdvisedSupport advisedSupport) {
        this.advised = advisedSupport;
    }

    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method m = this.advised.getTargetClass().getMethod(method.getName(),method.getParameterTypes());
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(m,this.advised.getTargetClass());
        QMethodInvocation methodInvocation = new QMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),chain);
        return methodInvocation.proceed();
    }
}
