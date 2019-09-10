package com.qcj.spring.framework.core;

/**
 * Created by qcj on 2019-09-10
 */
public interface BeanFactory {

    /**
     * 根本beanName获取ioc中的实例bean
     * @param name
     * @return
     */
    Object getBean(String name);
}
