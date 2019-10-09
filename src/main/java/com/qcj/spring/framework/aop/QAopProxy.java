package com.qcj.spring.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by qcj on 2019-09-18
 */
public interface QAopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
