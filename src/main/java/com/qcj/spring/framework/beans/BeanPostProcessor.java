package com.qcj.spring.framework.beans;

import com.qcj.spring.framework.core.FactoryBean;

/**
 * 用于做事件监听的
 * Created by qcj on 2019-09-10
 */
public class BeanPostProcessor extends FactoryBean {

    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }


    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
