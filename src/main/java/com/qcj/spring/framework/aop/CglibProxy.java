package com.qcj.spring.framework.aop;

import com.qcj.spring.framework.aop.support.QAdvisedSupport;

/**
 * Created by qcj on 2019-09-18
 */
public class CglibProxy implements QAopProxy {
    public CglibProxy(QAdvisedSupport config) {
    }

    public Object getProxy() {
        return null;
    }

    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
