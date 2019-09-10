package com.qcj.spring.framework.beans;

import com.qcj.spring.framework.core.FactoryBean;

/**
 * Created by qcj on 2019-09-10
 */
public class BeanWrapper extends FactoryBean {

    //还会用到 观察者 模式
    //1、支持事件响应，会有一个监听
    private BeanPostProcessor beanPostProcessor;

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }

    private Object wrapperInstance;

    //原生的通过反射得来的对象，保护起来
    private Object originalInstance;

    public BeanWrapper(Object instance){
        this.wrapperInstance = instance;
        this.originalInstance = instance;
    }

    public Object getWrapperInstance(){
        return this.wrapperInstance;
    }

    //返回代理后的类
    public Class<?> getWrapperClass(){
        return this.wrapperInstance.getClass();
    }
}
